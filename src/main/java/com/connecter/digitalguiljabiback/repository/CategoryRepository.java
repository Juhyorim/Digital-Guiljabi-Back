package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  String findAncestors = """
    select * from category c where not exists(
        select 1 from clo_category cc where cc.descendant = c.pk and cc.ancestor != c.pk
      );
    """;

  String findMyAncestors = """
    SELECT c.* FROM category c
    INNER JOIN (
      SELECT ancestor, depth FROM clo_category
      WHERE descendant = ?1
    ) as ct
    ON c.pk = ct.ancestor
    ORDER BY ct.depth DESC;
    """;

  @Procedure(procedureName = "insertCategory")
  void addConnect(@Param("cur_idx") Long categoryPk, @Param("parent_idx")Long parentCategoryPk);

  @Procedure(procedureName = "CdeleteCategory")
  void deleteConnect(@Param("cur_idx") Long categoryPk);

  @Procedure(name = "updateCategory")
  void updateConnect(@Param("cur_idx") Long categoryPk, @Param("parent_idx") Long parentCategoryPk);

  @Query(value = findAncestors, nativeQuery = true)
  Optional<List<Category>> findFirstAncestor();

  @Query(value = "SELECT * FROM category WHERE pk IN (SELECT descendant FROM clo_category WHERE ancestor = ?1 AND depth=1)", nativeQuery = true)
  Optional<List<Category>> findChildren(Long pk); //직속 자식 찾기

  //나를 제외한 모든 나의 조상 찾기(가장 조상부터 내림차순으로 정렬)
  @Query(value = findMyAncestors, nativeQuery = true)
  Optional<List<Category>> findMyAncestor(Long pk);
}
