package com.mohit.tracker.CoronavirusTracker.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mohit.tracker.CoronavirusTracker.Repository.LocationStats;

@Service
public class TrackerService {

	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

	private List<LocationStats> allStats = new ArrayList<LocationStats>();

	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchVirusData() {

		List<LocationStats> newStats = new ArrayList<LocationStats>();
		try {
			URL url = new URL(VIRUS_DATA_URL);
			URLConnection urlConnection = url.openConnection();
			HttpURLConnection connection = null;
			if (urlConnection instanceof HttpURLConnection) {
				connection = (HttpURLConnection) urlConnection;
			} else {
				System.out.println("Please enter an HTTP URL.");
				return;
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

			for (CSVRecord record : records) {
				LocationStats locationStats = new LocationStats();
				locationStats.setState(record.get("Province/State"));
				locationStats.setCountry(record.get("Country/Region"));
				int latestCases = Integer.parseInt(record.get(record.size() - 1));
				int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
				locationStats.setLatestTotalCases(latestCases);
				locationStats.setDiffFromPrevDay(latestCases - prevDayCases);
				newStats.add(locationStats);
			}

			this.allStats = newStats;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<LocationStats> getAllStats() {
		return allStats;
	}

}
