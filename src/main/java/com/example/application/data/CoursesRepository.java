package com.example.application.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CoursesRepository extends JpaRepository<Courses, Long>, JpaSpecificationExecutor<Courses> {

	@Override
	@EntityGraph(attributePaths = {"teacher", "students"})
	Page<Courses> findAll(Pageable pageable);

	@Override
	@EntityGraph(attributePaths = {"teacher", "students"})
	java.util.List<Courses> findAll();

	@Modifying
	@Query("update Courses c set c.teacher = null where c.teacher.id = :teacherId")
	void clearTeacherCourses(@Param("teacherId") Long teacherId);

}
