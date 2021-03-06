package com.group7.voluntaweb.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;
import com.group7.voluntaweb.components.GenericComponent;
import com.group7.voluntaweb.components.UserComponent;
import com.group7.voluntaweb.models.Category;
import com.group7.voluntaweb.models.Like;
import com.group7.voluntaweb.models.ONG;
import com.group7.voluntaweb.models.User;
import com.group7.voluntaweb.models.UsersVolunteerings;
import com.group7.voluntaweb.models.Volunteering;
import com.group7.voluntaweb.repositories.LikeRepository;
import com.group7.voluntaweb.repositories.ONGRepository;
import com.group7.voluntaweb.repositories.UserRepository;
import com.group7.voluntaweb.repositories.VolunteeringRepository;
import com.group7.voluntaweb.services.ImageService;
import com.group7.voluntaweb.services.UserService;
import com.group7.voluntaweb.services.VolunteeringService;

@RestController
@RequestMapping("/api/volunteerings")

public class VolunteeringRestController {
	interface CompleteVolunteering extends Volunteering.Basico, Category.Basico {
	}

	interface CompleteVolunteering2 extends Volunteering.Basico, Volunteering.NGO, Volunteering.Cat, ONG.Basico,
			Category.Basico, Volunteering.Likes, Like.Basico {
	}

	interface CompleteVolunteering3 extends Volunteering.Basico {
	}

	Date date = new Date();

	@Autowired
	private GenericComponent genCompo;

	@Autowired
	private VolunteeringService volunteeringService;

	@Autowired
	private UserComponent userComponent;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ONGRepository ongRepo;
	@Autowired
	private VolunteeringRepository volRepo;

	@Autowired
	private LikeRepository likeRepo;

	@Autowired
	private ImageService imgService;

	// volunteering's list
	@GetMapping("/")
	@JsonView(CompleteVolunteering.class)
	public Collection<Volunteering> getVolunteerings(@RequestParam(value = "page", required = false) Integer page) {
		Iterable<Volunteering> volunteerings;
		if (page != null) {
			volunteerings = volunteeringService.volunteeringByPage(page, 5);
		} else {
			volunteerings = volunteeringService.volunteeringByPage(0, 5);
		}
		List<Volunteering> list = StreamSupport.stream(volunteerings.spliterator(), false).collect(Collectors.toList());
		return list;
	}

	@GetMapping("/all/")
	@JsonView(CompleteVolunteering.class)
	public Collection<Volunteering> getAllVolunteerings() {
		return volunteeringService.findAll();
	}

	// obtain a volunteering
	@GetMapping("/{id}")
	@JsonView(CompleteVolunteering2.class)
	public ResponseEntity<Volunteering> getVolunteeringId(@PathVariable long id) {

		Volunteering volunteering = volunteeringService.findVolunteering(id);
		if (volunteering != null) {
			return new ResponseEntity<Volunteering>(volunteering, HttpStatus.OK);
		} else {
			return new ResponseEntity<Volunteering>(HttpStatus.NOT_FOUND);
		}
	}

	// create a new volunteering
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@JsonView(CompleteVolunteering2.class)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Volunteering> createVolunteering(@RequestBody Volunteering ad) {

		if (genCompo.isLoggedONG()) {
			ONG ngo = (ONG) genCompo.getLoggedUser();
			ad.setOng(ngo);
			Volunteering saved = volunteeringService.save(ad);
			ngo.getVolunteerings().add(ad);
			return new ResponseEntity<Volunteering>(saved, HttpStatus.CREATED);
		}

		return new ResponseEntity<Volunteering>(HttpStatus.UNAUTHORIZED);

	}

	// delete volunteering
	@DeleteMapping("/{id}")
	@JsonView(CompleteVolunteering.class)
	public ResponseEntity<Volunteering> deleteVolunteering(@PathVariable Long id) {
		// Boolean isONG = ongComponent.getLoggedUser() != null;

		Volunteering deletedVolunteering = volunteeringService.findVolunteering(id); // .get()
		if (this.genCompo.getLoggedUser() instanceof ONG) {

			ONG ong = (ONG) this.genCompo.getLoggedUser();

			if (ong.getId().equals(deletedVolunteering.getOng().getId())) {
				if (deletedVolunteering != null) {
					volunteeringService.delete(id);
					return new ResponseEntity<Volunteering>(deletedVolunteering, HttpStatus.OK);
				} else {
					return new ResponseEntity<Volunteering>(HttpStatus.NOT_FOUND);
				}
			} else {
				return new ResponseEntity<Volunteering>(HttpStatus.UNAUTHORIZED);
			}

		} else if (this.genCompo.getLoggedUser() instanceof User) {

			User user = (User) this.genCompo.getLoggedUser();

			if (user.getRoles().contains("ROLE_ADMIN")) {
				volunteeringService.delete(id);
				return new ResponseEntity<Volunteering>(deletedVolunteering, HttpStatus.OK);
			} else {
				return new ResponseEntity<Volunteering>(HttpStatus.UNAUTHORIZED);
			}
		} else {
			return new ResponseEntity<Volunteering>(HttpStatus.UNAUTHORIZED);
		}

	}

	// update a volunteering
	@PutMapping("/{id}")
	@JsonView(CompleteVolunteering2.class)
	public ResponseEntity<Volunteering> deleteVolunteering(@PathVariable long id,
			@RequestBody Volunteering updatedVolunteering) {

		if (volunteeringService.findVolunteering(id) != null) {
			updatedVolunteering.setId(id);
			if (this.genCompo.getLoggedUser() instanceof ONG) {
				ONG ngo = (ONG) genCompo.getLoggedUser();
				if (ngo != null && volunteeringService.findVolunteering(id).getOng().getId().equals(ngo.getId())) {
					updatedVolunteering.setOng(ngo);
					volunteeringService.save(updatedVolunteering);
					return new ResponseEntity<Volunteering>(updatedVolunteering, HttpStatus.OK);

				} else {
					return new ResponseEntity<Volunteering>(HttpStatus.UNAUTHORIZED);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}

		} else {
			return new ResponseEntity<Volunteering>(HttpStatus.NOT_FOUND);
		}

	}

	// joining to a volunteering
	@PostMapping("/join/{id}")
	@JsonView(CompleteVolunteering2.class)
	public ResponseEntity<Object> joiningVolunteering(@PathVariable Long id) {

		Boolean isUser = genCompo.getLoggedUser() != null;
		Volunteering vol = volunteeringService.findVolunteering(id);
		User user = (User) genCompo.getLoggedUser();

		if (isUser && !user.getRoles().contains("ROLE_ADMIN")) {

			UsersVolunteerings userFound = userService.findVolUser(vol.getId(), user.getId());
			if (userFound == null) {
				UsersVolunteerings connect = new UsersVolunteerings();
				connect.setUser(user);
				connect.setVolunteering(vol);
				connect.setDate(new Timestamp(new Date().getTime()));
				volunteeringService.join(connect);
				return new ResponseEntity<>(true, HttpStatus.OK);
			} else {

				volunteeringService.deleteJoin(user.getId(), vol.getId());
				return new ResponseEntity<>(false, HttpStatus.OK);
			}

		} else if (!isUser || user.getRoles().contains("ROLE_ADMIN")) {
			System.out.println(isUser);
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		} else {
			System.out.println("test");
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		}

	}

	// like volunteering
	@PostMapping("/like/{id}")
	@JsonView(CompleteVolunteering2.class)
	public ResponseEntity<Object> likeVolunteering(@PathVariable long id) {

		Boolean isUser = userComponent.getLoggedUser() != null;
		Volunteering vol = volunteeringService.findVolunteering(id);
		User user = userComponent.getLoggedUser();

		if (isUser && !user.getRoles().contains("ROLE_ADMIN")) {

			if (volunteeringService.findLike(vol, user) == null) {
				Like like = new Like();

				like.setUser(user);
				like.setVolunteering(vol);
				volunteeringService.like(like);

				return new ResponseEntity<>(true, HttpStatus.OK);
			} else {

				likeRepo.deleteLike(vol, user);
				return new ResponseEntity<>(false, HttpStatus.OK);

			}

		} else if (!isUser || user.getRoles().contains("ROLE_ADMIN")) {

			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		} else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

	}

	// Only logged users
	@JsonView(CompleteVolunteering3.class)
	@PostMapping(value = "/image/{id}")
	public ResponseEntity<Volunteering> uploadImage(@PathVariable Long id, @RequestParam MultipartFile file0)
			throws IOException {

		ONG ngo = (ONG) this.genCompo.getLoggedUser();

		Volunteering volunteering = this.volRepo.findById((long) id);

		if (ngo.getId().equals(volunteering.getOng().getId())) {

			Path path = this.imgService.saveImage("volunteerings", file0);
			String filePath = path.getFileName().toString();
			volunteering.setImage(filePath);
			this.volRepo.save(volunteering);

			return new ResponseEntity<>(volunteering, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// Anonymous
	@JsonView(CompleteVolunteering3.class)
	@GetMapping(value = "/image/{filename}")
	public ResponseEntity<Object> downloadImage(@PathVariable String filename)
			throws MalformedURLException, FileNotFoundException {
		return this.imgService.createResponseFromImage("volunteerings", filename);
	}

	@GetMapping("/ong/{id}")
	// @JsonView(CompleteVolunteering2.class)
	public ResponseEntity<Object> getVolunteeringsByNGO(@PathVariable Long id) {
		ONG ngo = ongRepo.findByid(id);
		Iterable<Volunteering> volunteerings = volRepo.findVolunteeringsByNGO(ngo);

		if (volunteerings != null) {
			return new ResponseEntity<>(volunteerings, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/join/{id}")
	// @JsonView(CompleteVolunteering2.class)
	public ResponseEntity<Object> getJoinedVolunteeringByUser(@PathVariable Long id) {
		User user = userRepo.findByid(id);
		Iterable<Volunteering> volunteerings = volRepo.findMyVolunteerings(user);

		if (volunteerings != null) {
			return new ResponseEntity<>(volunteerings, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/like/{id}")
	// @JsonView(CompleteVolunteering2.class)
	public ResponseEntity<Object> getLikedVolunteeringByUser(@PathVariable Long id) {
		User user = userRepo.findByid(id);
		Iterable<Volunteering> volunteerings = volRepo.findMyLiked(user);

		if (volunteerings != null) {
			return new ResponseEntity<>(volunteerings, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}