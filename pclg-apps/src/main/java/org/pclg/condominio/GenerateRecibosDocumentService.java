package org.pclg.condominio;


import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.PropertiesHelper;
import org.pclg.tools.ToolBox;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;

public class GenerateRecibosDocumentService {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    private GenerateRecibosDocumentService() {
    }

    public static void main(final String[] args) throws IOException, ParseException {
        createReport();
        LOGGER.warn("No olvidar conectar la red correcta para imprimir :p");
    }

    private static void createReport() throws IOException, ParseException {
        final Properties properties = new Properties();
        PropertiesHelper.loadPropertiesFromClasspath(properties, "Encajeras.properties");

        final EncajerasReportingData reportingData = buildEncajerasReportingData(properties);

        final GenerateEncajerasDocxService generateDocxService = new GenerateEncajerasDocxService();

        final ByteArrayOutputStream byteArrayOutputStream = generateDocxService
            .generateDocx(reportingData, properties.getProperty("Template.Name"));
        try (final FileOutputStream fos = new FileOutputStream(reportingData.getOutputPath())) {
            byteArrayOutputStream.writeTo(fos);
        }
    }

    private static EncajerasReportingData buildEncajerasReportingData(final Properties properties) throws ParseException {
        final EncajerasReportingData reportingData = new EncajerasReportingData();
        // Info recibos
        setUpReceiptNumbers(properties, reportingData);

        // Fechas
        final String receiptDateStr = properties.getProperty("receiptDate");
        final SimpleDateFormat shortDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        final Date receiptDate = shortDateFormat.parse(receiptDateStr);
//        final LocalDate receiptDate = LocalDate.parse(receiptDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        final MessageFormat messageFormat = new MessageFormat(properties.getProperty("Output.Path"));
        reportingData.setOutputPath(messageFormat.format(new Object[] {receiptDate}));

        final Locale localeEs = new Locale("es", "SP");
        final SimpleDateFormat longDateFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", localeEs);
        final String longDate = longDateFormat.format(receiptDate);
        reportingData.setFechaLarga(longDate);

        final String shortDate = shortDateFormat.format(receiptDate);
        reportingData.setFechaCorta(shortDate);

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(receiptDate);
        calendar.add(Calendar.MONTH, -1);
        final String startDate = shortDateFormat.format(calendar.getTime());
        reportingData.setFechaInicio(startDate);

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        final String endDate = shortDateFormat.format(calendar.getTime());
        reportingData.setFechaFin(endDate);

        // Estado de cuentas
        final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        final NumberFormat numberFormat = new DecimalFormat("###,##0.00", symbols);

        final BigDecimal startBankBalance = new BigDecimal(properties.getProperty("startBankBalance"));
        reportingData.setSaldoInicioBanco(numberFormat.format(startBankBalance));
        final BigDecimal startCashBalance = new BigDecimal(properties.getProperty("startCashBalance"));
        reportingData.setSaldoInicioCash(numberFormat.format(startCashBalance));
        final BigDecimal startTotal = startBankBalance.add(startCashBalance);
        reportingData.setTotalInicio(numberFormat.format(startTotal));
        final BigDecimal endBankBalance = new BigDecimal(properties.getProperty("endBankBalance"));
        reportingData.setSaldoFinBanco(numberFormat.format(endBankBalance));
        final BigDecimal endCashBalance = new BigDecimal(properties.getProperty("endCashBalance"));
        reportingData.setSaldoFinCash(numberFormat.format(endCashBalance));
        final BigDecimal endTotal = endBankBalance.add(endCashBalance);
        reportingData.setTotalFin(numberFormat.format(endTotal));

        class MutableBigDecimal {
            private BigDecimal value;

            private MutableBigDecimal(final BigDecimal value) {
                this.value = value;
            }

            private void add(final BigDecimal operand) {
                value = value.add(operand);
            }

            @Override
            public String toString() {
                return String.valueOf(value);
            }
        }

        class ExpensesLines {
            class Line {
                private final String provider;
                private final String amount;

                Line(final String provider, final String amount) {
                    this.provider = provider;
                    this.amount = amount;
                }
            }

            private final List<Line> lines = new ArrayList<>();
            private int maxProviderLength;
            private int maxAmountLength;

            private void addLine(final String provider, final String amount) {
                final Line line = new Line(provider, amount);
                lines.add(line);
                int length = provider.length();
                if (length > maxProviderLength) {
                    maxProviderLength = length;
                }

                length = amount.length();
                if (length > maxAmountLength) {
                    maxAmountLength = length;
                }
            }

            @Override
            public String toString() {
                return lines.stream().map(line -> ToolBox.pad(line.provider, maxProviderLength, ' ')
                    + ToolBox.leftPad(line.amount, maxAmountLength, ' '))
                    .collect(Collectors.joining("\n"));
            }
        }

        final ExpensesLines expensesLines = new ExpensesLines();
        final MutableBigDecimal totalExpenses = new MutableBigDecimal(BigDecimal.ZERO);
        Arrays.stream(properties.getProperty("expenseLines").split("\\|")).filter(line -> !line.trim().startsWith("#"))
            .forEach(line -> {
                final int lastIndexOf = line.lastIndexOf(' ') + 1;
                if (lastIndexOf > 0) {
                    final String provider = line.substring(0, lastIndexOf);
                    final BigDecimal amount = new BigDecimal(line.substring(lastIndexOf));
                    totalExpenses.add(amount);
                    expensesLines.addLine(provider, numberFormat.format(amount));
                }
            });

        LOGGER.debug("expensesLines = \n" + expensesLines);
        LOGGER.debug("totalExpenses = " + totalExpenses);
        reportingData.setGastos(expensesLines.toString());

        final BigDecimal communityIn = new BigDecimal(properties.getProperty("communityIn"));
        reportingData.setIngresosComunidad(numberFormat.format(communityIn));
        final BigDecimal waterIn = new BigDecimal(properties.getProperty("waterIn"));
        reportingData.setIngresosAgua(numberFormat.format(waterIn));

        final BigDecimal communityOwed = new BigDecimal(properties.getProperty("communityOwed"));
        reportingData.setPendienteComunidad(numberFormat.format(communityOwed));
        final BigDecimal waterOwed = new BigDecimal(properties.getProperty("waterOwed"));
        reportingData.setPendienteAgua(numberFormat.format(waterOwed));
        final BigDecimal totalOwed = communityOwed.add(waterOwed);
        reportingData.setPendienteTotal(numberFormat.format(totalOwed));

        if (startTotal.add(communityIn).add(waterIn).subtract(totalExpenses.value)
            .subtract(endTotal).compareTo(BigDecimal.ZERO) != 0) {
            throw new RuntimeException("¡Danger, danger: inconsistencia en los valores!");
        }

        return reportingData;
    }

    private static void setUpReceiptNumbers(final Properties properties,
                                            final EncajerasReportingData reportingData) {
        int lastReceiptNr = Integer.parseInt(properties.getProperty("lastReceiptNr", "0"));
        reportingData.setNumeroRecibo1(String.valueOf(++lastReceiptNr));
        reportingData.setNumeroRecibo2(String.valueOf(++lastReceiptNr));
        reportingData.setNumeroRecibo3(String.valueOf(++lastReceiptNr));
        reportingData.setNumeroRecibo4(String.valueOf(++lastReceiptNr));
        reportingData.setNumeroRecibo5(String.valueOf(++lastReceiptNr));
        reportingData.setNumeroRecibo6(String.valueOf(++lastReceiptNr));
    }
}
