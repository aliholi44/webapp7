package com.group7.voluntaweb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.group7.voluntaweb.components.GenericComponent;
import com.group7.voluntaweb.helpers.Helpers;
import com.group7.voluntaweb.models.Comment;
import com.group7.voluntaweb.models.ONG;
import com.group7.voluntaweb.models.User;
import com.group7.voluntaweb.repositories.CommentRepository;

@Controller
public class CommentController {

	@Autowired
	private CommentRepository commentRepo;

	@Autowired
	GenericComponent genCompo;

	@GetMapping("/contact")
	public String contact(Model model) {
//		Authentication principal = SecurityContextHolder.getContext().getAuthentication();
//		String currentPrincipalName = principal.getName();
//		User user = userRepo.findByEmail(currentPrincipalName);

		Helpers helper = new Helpers();
		if (genCompo.getLoggedUser() instanceof User) {
			User user = (User) genCompo.getLoggedUser();
			Boolean isAdmin = user.getRoles().contains("ROLE_ADMIN");
			helper.setNavbar(model, user, null, isAdmin);

		} else if (genCompo.getLoggedUser() instanceof ONG) {
			ONG ong = (ONG) genCompo.getLoggedUser();
			helper.setNavbar(model, null, ong, false);
		}
		model.addAttribute("title", "Contacta con nosotros");
		return "contact";
	}

	@PostMapping("/new-message")
	public String message(@RequestParam String name, @RequestParam String email, @RequestParam String message) {

		Comment comment = new Comment(name, email, message);

		commentRepo.save(comment);

		return "redirect:/contact";
	}

}
