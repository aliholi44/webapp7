package com.group7.voluntaweb.Repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.group7.voluntaweb.Models.Like;
import com.group7.voluntaweb.Models.Volunteering;

public interface LikeRepository extends JpaRepository<Like, Long> {
	
	@Query("SELECT l FROM Like l WHERE volunteering_id = :volunteering_id AND user_id = :user_id")
	Like findLike(@Param("volunteering_id") long volunteering_id, @Param("user_id") long user_id);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM Like WHERE volunteering_id = :volunteering_id AND user_id = :user_id")
	void deleteLike(@Param("volunteering_id") long volunteering_id, @Param("user_id") long user_id);

	
	@Query("SELECT COUNT(l) FROM Like l WHERE volunteering_id = :volunteering_id ")
	long countLike(@Param("volunteering_id") long volunteering_id);
	
}


