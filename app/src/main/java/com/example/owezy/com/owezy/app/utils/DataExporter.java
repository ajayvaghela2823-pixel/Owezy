package com.owezy.app.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.core.content.FileProvider;

import com.owezy.app.database.entities.Expense;
import com.owezy.app.database.entities.Group;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for exporting app data to various formats
 */
public class DataExporter {

    private static final SimpleDateFormat DATE_FORMAT = 
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    /**
     * Export expenses to CSV format
     */
    public static File exportToCSV(Context context, List<Expense> expenses, List<Group> groups) 
            throws IOException {
        File exportDir = getExportDirectory(context);
        String fileName = "Owezy_Export_" + System.currentTimeMillis() + ".csv";
        File csvFile = new File(exportDir, fileName);

        FileWriter writer = new FileWriter(csvFile);
        
        // CSV Header
        writer.append("Date,Description,Amount,Category,Group,Paid By\n");

        // CSV Data
        for (Expense expense : expenses) {
            String groupName = getGroupName(groups, expense.getGroupId());
            String date = DATE_FORMAT.format(new Date(expense.getDate()));
            
            writer.append(escapeCsvValue(date)).append(",");
            writer.append(escapeCsvValue(expense.getDescription())).append(",");
            writer.append(String.valueOf(expense.getAmount())).append(",");
            writer.append(escapeCsvValue(expense.getCategory())).append(",");
            writer.append(escapeCsvValue(groupName)).append(",");
            writer.append(String.valueOf(expense.getPaidById())).append("\n");
        }

        writer.flush();
        writer.close();

        return csvFile;
    }

    /**
     * Export expenses to JSON format
     */
    public static File exportToJSON(Context context, List<Expense> expenses, List<Group> groups) 
            throws IOException, JSONException {
        File exportDir = getExportDirectory(context);
        String fileName = "Owezy_Export_" + System.currentTimeMillis() + ".json";
        File jsonFile = new File(exportDir, fileName);

        JSONObject root = new JSONObject();
        root.put("exportDate", DATE_FORMAT.format(new Date()));
        root.put("appVersion", "2.0");

        // Groups array
        JSONArray groupsArray = new JSONArray();
        for (Group group : groups) {
            JSONObject groupObj = new JSONObject();
            groupObj.put("id", group.getId());
            groupObj.put("name", group.getName());
            groupObj.put("icon", group.getIcon());
            groupObj.put("totalAmount", group.getTotalAmount());
            groupObj.put("settled", group.isSettled());
            groupsArray.put(groupObj);
        }
        root.put("groups", groupsArray);

        // Expenses array
        JSONArray expensesArray = new JSONArray();
        for (Expense expense : expenses) {
            JSONObject expenseObj = new JSONObject();
            expenseObj.put("id", expense.getId());
            expenseObj.put("description", expense.getDescription());
            expenseObj.put("amount", expense.getAmount());
            expenseObj.put("category", expense.getCategory());
            expenseObj.put("date", DATE_FORMAT.format(new Date(expense.getDate())));
            expenseObj.put("paidBy", expense.getPaidById());
            expenseObj.put("groupId", expense.getGroupId());
            expensesArray.put(expenseObj);
        }
        root.put("expenses", expensesArray);

        // Write to file
        FileWriter writer = new FileWriter(jsonFile);
        writer.write(root.toString(2)); // Pretty print with 2 spaces indentation
        writer.flush();
        writer.close();

        return jsonFile;
    }

    /**
     * Share exported file
     */
    public static void shareFile(Context context, File file) {
        Uri fileUri = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".provider",
                file
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(getMimeType(file));
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        
        context.startActivity(Intent.createChooser(shareIntent, "Share Export"));
    }

    /**
     * Get or create export directory
     */
    private static File getExportDirectory(Context context) throws IOException {
        File exportDir = new File(context.getExternalFilesDir(null), "Exports");
        if (!exportDir.exists()) {
            if (!exportDir.mkdirs()) {
                throw new IOException("Failed to create export directory");
            }
        }
        return exportDir;
    }

    /**
     * Get group name by ID
     */
    private static String getGroupName(List<Group> groups, long groupId) {
        for (Group group : groups) {
            if (group.getId() == groupId) {
                return group.getName();
            }
        }
        return "Unknown";
    }

    /**
     * Escape CSV values to handle commas and quotes
     */
    private static String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Get MIME type for file
     */
    private static String getMimeType(File file) {
        String fileName = file.getName();
        if (fileName.endsWith(".csv")) {
            return "text/csv";
        } else if (fileName.endsWith(".json")) {
            return "application/json";
        }
        return "*/*";
    }

    /**
     * Get export summary (count of items exported)
     */
    public static String getExportSummary(int expenseCount, int groupCount) {
        return String.format(Locale.getDefault(), 
                "Exported %d expenses from %d groups", 
                expenseCount, groupCount);
    }
}
