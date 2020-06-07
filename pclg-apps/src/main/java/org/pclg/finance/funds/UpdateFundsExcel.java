package org.pclg.finance.funds;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Formula;
import jxl.write.Number;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.pclg.annotations.QuickAndDirty;
import org.pclg.log.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
@QuickAndDirty
public class UpdateFundsExcel {
    private static final Logger LOGGER = LoggerFactory.make();
    private static final Pattern PATTERN =
		Pattern.compile("(\\D+)(\\S+)(\\s+)(\\S+)(\\s+)(\\S+)(\\s+)(\\S+)(\\s+)"
				+ "(\\S+)(\\s+)(\\S+)(\\s+)(\\S+)(\\s+)(\\S+)(\\s+)(\\S+)");
	private static final int FIRST_EXCEL_DATA_ROW = 2;
	private static final int MOV_AVG_10_COL = 3;
	private static final int MOV_AVG_30_COL = 4;
	private static final int DELTA_COL = 2;
	private static final int VALUE_COL = 1;
	private static final int DATE_COL = 0;
	private static final int TOTAL_DELTA_COL = 5;
	private static final int TOTAL_PERCENT_DELTA_COL = 6;
	private static final String VENICE_TXT = "Venice.txt";
	private final File dataFile;
    private final File inFile;
    private final File outFile;
    private final SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy",
            Locale.US);
    private final SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd",
            Locale.US);
    private final WritableWorkbook copy;
    //private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    //private final PrintStream stream = new PrintStream(out);
	private final DateFormat customDateFormat = new DateFormat("dd/MM/yyyy");
	private final WritableCellFormat dateFormat = new WritableCellFormat(
         customDateFormat);
	private final WritableCellFormat floatFormat =
         new WritableCellFormat(NumberFormats.ACCOUNTING_RED_FLOAT);
	private final WritableCellFormat percentFormat =
         new WritableCellFormat(NumberFormats.PERCENT_FLOAT);
	private final PrintWriter veniceWriter;

	private UpdateFundsExcel(final String dataFileName, final String excelFile)
            throws BiffException, IOException {
        dataFile = new File(dataFileName);
        inFile = new File(excelFile);
        outFile = new File(excelFile + "-out.xls");
		final File veniceFile = new File(inFile.getParentFile(), VENICE_TXT);
        veniceWriter = new PrintWriter(new FileWriter(veniceFile, true));
		LOGGER.info("Actualizando " + inFile + " y " + veniceFile);
        final WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("es", "ES"));
        final Workbook workbook = Workbook.getWorkbook(inFile, wbSettings);
        copy = Workbook.createWorkbook(outFile, workbook, wbSettings);
    }

    private void readFile() throws IOException, WriteException, ParseException {
        final BufferedReader reader = new BufferedReader(new FileReader(dataFile));
		//noinspection TryFinallyCanBeTryWithResources
		try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("FF CHINA FOCUS ")) {
                    continue;
                }
				final Matcher matcher = PATTERN.matcher(line);
				if (matcher.find()) {		// ¿Tiene sentido esta pregunta?
					final int count = matcher.groupCount();
	                if (count != 18) {
	                    //out.reset();
						//ArrayTools.printArray(stream, strings);
	                    //LOGGER.warning(">>>>> line = " + out.toString() + "  >>>>> length = " + strings.length);
	                    LOGGER.warning(">>>>> line = " + line + "  >>>>> length = " + count);
	                } else {
                        final String tabla = matcher.group(1).trim();// + '$';
                        final String group2 = matcher.group(2);
                        final String group18 = matcher.group(18);
                        update(tabla, new BigDecimal(group2.trim()),
                            group18.trim());
                        final String group14 = matcher.group(14);
                        updateVenice(group14, group18, group2);
	                }
				} else {
				    System.out.println("****  Doesn't match ******");
				}
            }

            copy.write();
            copy.close();
            if (inFile.delete()) {
                if (!outFile.renameTo(inFile)) {
                    LOGGER.severe("Warning: could not rename " + outFile
                            + " to " + inFile);
                }
            } else {
                LOGGER.severe("Warning: could not delete " + inFile);
            }
        } finally {
			veniceWriter.close();
            reader.close();
        }
    }

//	private void updateVenice(final String[] data) throws ParseException {
	private void updateVenice(final String symbol, final String date, final String price) throws ParseException {
		//PEPITO,20121231,10.0,50.0,5.0,20.0,89
		//FF CHINA FOCUS A ACC HKD 	9.62 		HKD 	0.08 	- 	0VQW 	B7LVP38 	LU0737861699 	n/a 	29-Nov-2012
		final float close = Float.parseFloat(price.trim());
		veniceWriter.printf(Locale.US, "%s,%s,%2.3f,%2.3f,%2.3f,%2.3f,%s\n",
			symbol.trim(),
			dateFormat2.format(dateFormat1.parse(date.trim())),
				close * .8,
				close * 1.5,
				close * .5,
				close,
			0);
	}

	private void update(final String tabla,
            final BigDecimal bigDecimal, final String strDate)
            throws ParseException, WriteException {
        final WritableSheet sheet = copy.getSheet(tabla);
        if (sheet == null) {
            LOGGER.severe("La tabla '" + tabla + "' no existe.");
            return;
        }
        final int row = sheet.getRows();

        final Date date = new Date(dateFormat1.parse(strDate).getTime());
        final DateTime dateTime = new DateTime(DATE_COL, row, date, dateFormat);
        sheet.addCell(dateTime);

        final Number number = new Number(VALUE_COL, row, bigDecimal.doubleValue(),
                floatFormat);
        sheet.addCell(number);

        final int excelRow = row + 1;    // In Excel rows are 1 - based

        final String delta = "B" + excelRow + "-B" + (excelRow - 1);
        final Formula deltaFormula = new Formula(DELTA_COL, row, delta, floatFormat);
        sheet.addCell(deltaFormula);

        if (excelRow > 10) {
            computeMovingAverage(sheet, MOV_AVG_10_COL, row, excelRow, floatFormat, 10);
            if (excelRow > 30) {
                computeMovingAverage(sheet, MOV_AVG_30_COL, row, excelRow, floatFormat, 30);
            }
        }

		final String totalDelta = "B" + excelRow + "-$B$" + FIRST_EXCEL_DATA_ROW;
		final Formula totalDeltaFormula =
			new Formula(TOTAL_DELTA_COL, row, totalDelta, floatFormat);
		sheet.addCell(totalDeltaFormula);

		final String totalPercentDelta =
			"(B" + excelRow + "-$B$" + FIRST_EXCEL_DATA_ROW
					+ ")/$B$" + FIRST_EXCEL_DATA_ROW;
		final Formula totalPercentDeltaFormula =
			new Formula(TOTAL_PERCENT_DELTA_COL, row, totalPercentDelta, percentFormat);
		sheet.addCell(totalPercentDeltaFormula);
    }

    private static void computeMovingAverage(final WritableSheet sheet, final int col, final int row,
            final int excelRow, final WritableCellFormat floatFormat, final int nrDays) throws WriteException {
        final String movAvg10 = "PROMEDIO(B" + (excelRow - (nrDays - 1)) + ":B" + excelRow + ")";
        final Formula movAvg10Formula = new Formula(col, row, movAvg10, floatFormat);
        sheet.addCell(movAvg10Formula);
    }

    public static void main(final String[] args) throws Exception {
        if (args.length != 2) {
            LOGGER.severe("Usage: java UpdateFundsExcel <input text file> "
					+ "<output excel file>.");
            System.exit(-1);
        }
        final UpdateFundsExcel updateFundsExcel = new UpdateFundsExcel(args[0], args[1]);
        updateFundsExcel.readFile();
    }
}

