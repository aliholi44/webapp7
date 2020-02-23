package com.group7.voluntaweb.Controllers;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import com.group7.voluntaweb.Models.ONG;
import com.group7.voluntaweb.Repositories.ONGRepository;
import com.group7.voluntaweb.Services.ONGService;

@Controller
public class ONGController {

	@Autowired
	private ONGRepository ongRepo;

	@Autowired
	private ONGService ongService;

	@GetMapping("/register-ong") // ONG REGISTER VIEW
	public String registerONG(Map<String, Object> model) {
		model.put("title", "Registrar ONG");

		return "registerONG"; // RETURNS registerONG.mustache
	}
	
	@GetMapping("/ongs")
	public String ngos(Model model) {
		model.addAttribute("title", "ong");
		Iterable<ONG> ngos = ongService.getAll();
		model.addAttribute("ngos", ngos);
		return "ongs";
	}

	@RequestMapping("/ongs/{id}")
	public String ngo(Map<String, Object> model, @PathVariable Long id) {
		ONG ngo = ongRepo.findByid(id);
		model.put("title", ngo.getName());
		model.put("name", ngo.getName());
		model.put("email", ngo.getEmail());
		model.put("telephone", ngo.getTelephone());
		model.put("address", ngo.getAddress());
		model.put("image", ngo.getImage());
		model.put("description", ngo.getDescription());
		return "ong-detail";
	}
	
	@PostMapping("/add-ong") // ONG REGISTER ACTION
	public String addOng(@RequestParam String name, @RequestParam String email, @RequestParam String responsible_name, @RequestParam String responsible_surname, @RequestParam String address, @RequestParam String telephone, @RequestParam String postal, String image, String password, @RequestParam String description, Map<String, Object> model) {
		model.put("title", "Registrar ONG");

		String enc_password = new BCryptPasswordEncoder().encode(password); // ENCRYPT PASSWORD

		ONG ong = new ONG(name, email, responsible_name, responsible_surname, address, telephone, postal, image, enc_password, description);

		this.ongService.save(ong); // INSERT INTO DATABASE

		return "redirect:index"; // REDIRECTS TO INDEX

	}

}