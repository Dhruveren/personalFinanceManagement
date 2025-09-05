package com.acme.pfm.cli.reports;

import com.acme.pfm.cli.services.interfaces.BudgetService;
import com.acme.pfm.cli.services.interfaces.TransactionService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;

import java.io.FileWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

public class ReportGenerator {
    private final TransactionService txService;
    private final BudgetService budgetService;
    private final Configuration fm;

    public ReportGenerator(TransactionService txService, BudgetService budgetService) {
        this.txService = txService;
        this.budgetService = budgetService;
        this.fm = freemarkerConfig();
    }

    private Configuration freemarkerConfig() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        return cfg;
    }

    public Path generateMonthly(String month, Path outHtml) throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Monthly Financial Report");
        model.put("month", month);
        String formatted = LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        model.put("generatedAt", formatted);


        var reportRows = budgetService.monthlyReport(month, true);
        BigDecimal totalSpend = reportRows.stream()
                .map(r -> r.spend).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalBudget = reportRows.stream()
                .map(r -> r.budget).reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> overview = new HashMap<>();
        overview.put("totalIncome", BigDecimal.ZERO);
        overview.put("totalExpense", totalSpend);
        overview.put("count", txService.getTotalCount());
        model.put("overview", overview);
        model.put("reportRows", reportRows);

        Path chartsDir = outHtml.getParent() == null ? Path.of(".") : outHtml.getParent();
        String baseName = outHtml.getFileName().toString().replaceAll("\\.html?$", "");

        Files.createDirectories(chartsDir);
        Map<String, String> charts = new HashMap<>();
        String svb = renderSpendVsBudgetChart(month, reportRows, chartsDir.resolve(baseName + "_spend_budget.png"));
        if (svb != null) charts.put("spendVsBudget", svb);
        String topCats = renderTopCategoriesChart(reportRows, chartsDir.resolve(baseName + "_top_categories.png"));
        if (topCats != null) charts.put("topCategories", topCats);
        model.put("charts", charts);

        Template tpl = fm.getTemplate("report.ftlh");
        try (FileWriter wr = new FileWriter(outHtml.toFile())) {
            tpl.process(model, wr);
        }
        return outHtml;
    }

    private String renderSpendVsBudgetChart(String month,
                                            List<BudgetService.BudgetReportRow> rows,
                                            Path outPng) {
        if (rows == null || rows.isEmpty()) return null;
        List<String> cats = new ArrayList<>();
        List<Double> bVals = new ArrayList<>();
        List<Double> sVals = new ArrayList<>();
        for (var r : rows) {
            cats.add(r.category);
            bVals.add(r.budget.doubleValue());
            sVals.add(r.spend.doubleValue());
        }
        CategoryChart chart = new CategoryChartBuilder()
                .width(700).height(400)
                .title("Spend vs Budget " + month)
                .xAxisTitle("Category").yAxisTitle("Amount")
                .build();
        Styler styler = chart.getStyler();
        styler.setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.addSeries("Budget", cats, bVals);
        chart.addSeries("Spend", cats, sVals);
        try {
            BitmapEncoder.saveBitmap(chart, outPng.toString(), BitmapEncoder.BitmapFormat.PNG);
            return outPng.getFileName().toString();
        } catch (Exception e) {
            return null;
        }
    }

    private String renderTopCategoriesChart(List<BudgetService.BudgetReportRow> rows,
                                            Path outPng) {
        if (rows == null || rows.isEmpty()) return null;
        var sorted = new ArrayList<>(rows);
        sorted.sort((a, b) -> b.spend.compareTo(a.spend));
        List<BudgetService.BudgetReportRow> top = sorted.size() > 6
                ? sorted.subList(0, 6)
                : sorted;

        PieChart chart = new PieChartBuilder()
                .width(600).height(400)
                .title("Top Categories by Spend")
                .build();
        PieStyler styler = chart.getStyler();
        styler.setLegendVisible(true);

        styler.setPlotContentSize(0.9);
        for (var r : top) {
            chart.addSeries(r.category, r.spend.doubleValue());
        }
        try {
            BitmapEncoder.saveBitmap(chart, outPng.toString(), BitmapEncoder.BitmapFormat.PNG);
            return outPng.getFileName().toString();
        } catch (Exception e) {
            return null;
        }
    }
}
