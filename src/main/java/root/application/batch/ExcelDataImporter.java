package root.application.batch;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import root.application.AccountService;
import root.application.CategoryService;
import root.application.PaymentService;
import root.application.command.CreatePayment;
import root.domain.Account;
import root.domain.Category;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.*;

import static java.util.Calendar.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExcelDataImporter implements CommandLineRunner
{
    private static final String DOCUMENT_PATH = "C://Users/Public/temp/budget-2020.xlsx";
    private static final int INITIAL_YEAR = 2020;
    private static final int INITIAL_MONTH = 1;
    private static final String ACCOUNT_ID = "d902be6c-7aee-11ea-8e8d-0242ac110002";
    private static final int HEADING_ROW_INDEX = 0;
    private static final String STOP_ROW_FIRST_CELL_VALUE = "Итого";
    private static final String LINE_SEPARATOR = "\n";
    private static final String PAYMENT_AMOUNT_DESCRIPTION_SEPARATOR = "-";
    private static final int MONTH_START_DAY = 5;
    private static final String TIME_ZONE = "UTC";
    private static final Date FAKE_DATE = new Date(1L);

    private final AccountService accountService;
    private final CategoryService categoryService;
    private final PaymentService paymentService;

    private Account account;
    private int year = INITIAL_YEAR;
    private int month = INITIAL_MONTH;
    private List<Date> rangeOfDates;
    private Map<Integer, String> columnIndexToCategoryIdMap;

    @Override
    public void run(String... args)
    {
        account = accountService.get(ACCOUNT_ID);
        Iterator<Sheet> sheetIterator = openExcelDocument(DOCUMENT_PATH).sheetIterator();
        while (sheetIterator.hasNext())
        {
            processSheet(sheetIterator.next());
        }
        log.info("-----------------------------");
        log.info("DATA IMPORT HAS BEEN FINISHED");
        log.info("-----------------------------");
    }

    @SneakyThrows
    private XSSFWorkbook openExcelDocument(String documentPath)
    {
        File file = new File(documentPath);
        FileInputStream fileInputStream = new FileInputStream(file);
        return new XSSFWorkbook (fileInputStream);
    }

    private void processSheet(Sheet sheet)
    {
        rangeOfDates = getRangeOfDates();
        columnIndexToCategoryIdMap = getColumnIndexToCategoryIdMap(sheet);
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext())
        {
            Row row = rowIterator.next();
            if (shouldStopSheetProcessing(row))
            {
                break;
            }
            processRow(row);
        }
        month++;
    }

    private void processRow(Row row)
    {
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext())
        {
            Cell cell = cellIterator.next();
            if (shouldStopCellProcessing(cell))
            {
                break;
            }
            if (cell.getColumnIndex() == 0 || !containsNumber(cell))
            {
                continue;
            }
            processCell(cell);
        }
    }

    private void processCell(Cell cell)
    {
        List<String> payments = extractPayments(cell);
        savePayments(payments, cell.getRowIndex(), cell.getColumnIndex());
    }

    private List<String> extractPayments(Cell cell)
    {
        Comment comment = cell.getCellComment();
        if (comment == null || comment.getString() == null)
        {
            log.error("No comment for cell [row: {}; column: {}], value [{}]",  cell.getRowIndex(), cell.getColumnIndex(), cell.getNumericCellValue());
            return List.of();
        }
        String commentString = comment.getString().getString();
        String[] commentLines = commentString.split(LINE_SEPARATOR);
        return Arrays.asList(commentLines).subList(0, commentLines.length - 1); // remove last line since it contains Author info (not payment)
    }

    private void savePayments(List<String> payments, int rowIndex, int columnIndex)
    {
        payments.forEach(payment -> {
            CreatePayment command = CreatePayment.builder()
                    .amount(getAmount(payment, rowIndex, columnIndex))
                    .description(getDescription(payment))
                    .date(rangeOfDates.get(rowIndex - 1))
                    .categoryId(columnIndexToCategoryIdMap.get(columnIndex))
                    .accountId(account.getId().toString())
                    .build();
            paymentService.execute(command);
        });
    }

    private BigDecimal getAmount(String payment, int rowIndex, int columnIndex)
    {
        try {
            return new BigDecimal(
                    payment.split(PAYMENT_AMOUNT_DESCRIPTION_SEPARATOR)[0].trim().replace(",", "."));
        }
        catch (Exception e)
        {
            log.error("Invalid payment string [row: {}; column: {}], payment [{}]",  rowIndex, columnIndex, payment);
            return BigDecimal.ZERO;
        }
    }

    private String getDescription(String payment)
    {
        return payment.split(PAYMENT_AMOUNT_DESCRIPTION_SEPARATOR)[1].trim();
    }

    private List<Date> getRangeOfDates()
    {
        TimeZone timeZone = TimeZone.getTimeZone(TIME_ZONE);
        GregorianCalendar startDate = new GregorianCalendar(timeZone);
        startDate.set(year, month - 1, MONTH_START_DAY);
        GregorianCalendar endDate = new GregorianCalendar(timeZone);
        endDate.set(year, month, MONTH_START_DAY);
        List<Date> rangeOfDates = new ArrayList<>();
        while (startDate.before(endDate))
        {
            // add fake days for months which are shorter then 31
            // for February
            if (startDate.get(DAY_OF_MONTH) == 1 && startDate.get(MONTH) == 2)
            {
                if (rangeOfDates.size() < 25) // not leap year
                {
                    rangeOfDates.add(FAKE_DATE);
                }
                rangeOfDates.add(FAKE_DATE);
                rangeOfDates.add(FAKE_DATE);
            }
            // for regular 30 day month
            if (startDate.get(DAY_OF_MONTH) == 1 && rangeOfDates.size() == 26)
            {
                rangeOfDates.add(FAKE_DATE);
            }
            rangeOfDates.add(startDate.getTime());
            startDate.add(DATE, 1);
        }
        return rangeOfDates;
    }

    private Map<Integer, String> getColumnIndexToCategoryIdMap(Sheet sheet)
    {
        Map<Integer, String> columnIndexToCategoryIdMap = new HashMap<>();
        Iterator<Cell> cellIterator = sheet.getRow(HEADING_ROW_INDEX).cellIterator();
        while (cellIterator.hasNext())
        {
            Cell cell = cellIterator.next();

            if (cell.getColumnIndex() == 0) continue;
            if (blank(cell)) break;
            if (!containsString(cell)) throw new IllegalStateException();

            Integer columnIndex = cell.getColumnIndex();
            String categoryName = cell.getStringCellValue();
            Category category = categoryService.resolveCategory(categoryName, account);
            columnIndexToCategoryIdMap.put(columnIndex, category.getId().toString());
        }
        return columnIndexToCategoryIdMap;
    }

    private boolean shouldStopSheetProcessing(Row row)
    {
        Iterator<Cell> cellIterator = row.cellIterator();
        if (cellIterator.hasNext())
        {
            Cell cell = cellIterator.next();
            return containsString(cell) &&
                    STOP_ROW_FIRST_CELL_VALUE.equals(cell.getStringCellValue());
        }
        return false;
    }

    private boolean shouldStopCellProcessing(Cell cell)
    {
        return cell.getColumnIndex() > columnIndexToCategoryIdMap.keySet().size();
    }

    private boolean containsString(Cell cell)
    {
        return cell.getCellType() == Cell.CELL_TYPE_STRING;
    }

    private boolean containsNumber(Cell cell)
    {
        return cell.getCellType() == Cell.CELL_TYPE_NUMERIC;
    }

    private boolean blank(Cell cell)
    {
        return cell.getCellType() == Cell.CELL_TYPE_BLANK;
    }
}
