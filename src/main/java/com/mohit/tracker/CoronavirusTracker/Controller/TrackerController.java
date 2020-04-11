package com.mohit.tracker.CoronavirusTracker.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mohit.tracker.CoronavirusTracker.Repository.LocationStats;
import com.mohit.tracker.CoronavirusTracker.Service.TrackerService;

@Controller
public class TrackerController {

	@Autowired
	TrackerService trackerService;

	@GetMapping("/")
	public String home(Model model) {
		List<LocationStats> allStats = trackerService.getAllStats();
		int totalCases = allStats.stream().mapToInt(p -> p.getLatestTotalCases()).sum();
		int totalNewCases = allStats.stream().mapToInt(p -> p.getDiffFromPrevDay()).sum();
		model.addAttribute("locationStats", allStats);
		model.addAttribute("totallReportedCases", totalCases);
		model.addAttribute("totalNewCases", totalNewCases);
		return "home";
	}

}
