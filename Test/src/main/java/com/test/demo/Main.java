package com.test.demo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class Main {
    public static void main(String[] args) throws Exception {
        List<CellPhone> cellPhoneList = readCellPhoneFile();
        List<CellPhoneUsageByMonth> cellPhoneUsageByMonthList = readCellPhoneUsageByMonthFile();

        populateHeaderSection(cellPhoneList, cellPhoneUsageByMonthList);
        populateDetailedSection(cellPhoneList, cellPhoneUsageByMonthList);
    }

    private static void populateHeaderSection(List<CellPhone> cellPhoneList, List<CellPhoneUsageByMonth> cellPhoneUsageByMonthList) {
        double totalMinutes = cellPhoneUsageByMonthList.stream().mapToDouble(e -> e.totalMinutes).sum();
        double totalData = cellPhoneUsageByMonthList.stream().mapToDouble(e -> e.totalData).sum();

        String sb = "{" + System.lineSeparator() +
                "  ReportRunDate=" + LocalDate.now() + System.lineSeparator() +
                "  NumberOfPhones" + cellPhoneList.stream().distinct().count() + System.lineSeparator() +
                "  TotalMinutes=" + totalMinutes + System.lineSeparator() +
                "  TotalData" + totalData + System.lineSeparator() +
                "  AverageMinutes=" + totalMinutes / cellPhoneUsageByMonthList.size() + System.lineSeparator() +
                "  AverageData=" + totalData / cellPhoneUsageByMonthList.size() + System.lineSeparator() +
                "}";

        System.out.println("=== Header Section ===");
        System.out.println(sb);
        System.out.println();
    }

    private static void populateDetailedSection(List<CellPhone> cellPhoneList, List<CellPhoneUsageByMonth> cellPhoneUsageByMonthList) {
        System.out.println("=== Detailed Section ===");

        for (CellPhone cellPhone : cellPhoneList) {
            Map<Month, Double> monthlyMinutesUsage = new TreeMap<>();
            Map<Month, Double> monthlyDataUsage = new TreeMap<>();

            cellPhoneUsageByMonthList.stream().filter(e -> e.employeeId == cellPhone.employeeId).forEach(e -> {
                Month key = e.date.getMonth();

                double totalMinutes = monthlyMinutesUsage.getOrDefault(key, 0d);
                double totalData = monthlyDataUsage.getOrDefault(key, 0d);
                monthlyMinutesUsage.put(key, totalMinutes + e.totalMinutes);
                monthlyDataUsage.put(key, totalData + e.totalData);
            });

            String sb = "{" + System.lineSeparator() +
                    "  EmployeeId=" + cellPhone.employeeId + System.lineSeparator() +
                    "  EmployeeName=" + cellPhone.employeeName + System.lineSeparator() +
                    "  Model=" + cellPhone.model + System.lineSeparator() +
                    "  PurchaseDate=" + cellPhone.purchaseDate + System.lineSeparator() +
                    "  MinuteUsage=" + monthlyMinutesUsage + System.lineSeparator() +
                    "  DataUsage=" + monthlyDataUsage + System.lineSeparator() +
                    "}" + System.lineSeparator();

            System.out.println(sb);
        }

    }

    private static List<CellPhone> readCellPhoneFile() throws IOException {
        List<CellPhone> cellPhoneList = new ArrayList<>();
        readFile("CellPhone.csv", (line) -> {
            String[] tokens = line.split(",");
            CellPhone cellPhone = new CellPhone();
            cellPhone.employeeId = Integer.parseInt(tokens[0]);
            cellPhone.employeeName = tokens[1];
            cellPhone.purchaseDate = tokens[2];
            cellPhone.model = tokens[3];
            cellPhoneList.add(cellPhone);
        });
        return cellPhoneList;
    }

    private static List<CellPhoneUsageByMonth> readCellPhoneUsageByMonthFile() throws IOException {
        List<CellPhoneUsageByMonth> cellPhoneUsageByMonthList = new ArrayList<>();
        readFile("CellPhoneUsageByMonth.csv", (line) -> {
            String[] tokens = line.split(",");
            CellPhoneUsageByMonth cellPhoneUsageByMonth = new CellPhoneUsageByMonth();
            cellPhoneUsageByMonth.employeeId = Integer.parseInt(tokens[0]);
            cellPhoneUsageByMonth.date = LocalDate.parse(tokens[1], DateTimeFormatter.ofPattern("M[M]/d[d]/yyyy"));
            cellPhoneUsageByMonth.totalMinutes = Double.parseDouble(tokens[2]);
            cellPhoneUsageByMonth.totalData = Double.parseDouble(tokens[3]);
            cellPhoneUsageByMonthList.add(cellPhoneUsageByMonth);
        });
        return cellPhoneUsageByMonthList;
    }

    private static void readFile(String file, Consumer<String> consumer) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        br.lines().skip(1).forEach(consumer);
        br.close();
    }
}