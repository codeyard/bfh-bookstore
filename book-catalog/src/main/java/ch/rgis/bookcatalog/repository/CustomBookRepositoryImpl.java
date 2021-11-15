package ch.rgis.bookcatalog.repository;

import ch.rgis.bookcatalog.entity.Book;
import ch.rgis.bookcatalog.entity.Book_;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class CustomBookRepositoryImpl implements CustomBookRepository {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Book> findBooksByKeywords(String... keywords) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);

        Root<Book> book = criteriaQuery.from(Book.class);

        Predicate conjunction = criteriaBuilder.conjunction();
        for (String keyword : keywords) {
            String findLike = "%" + keyword.toUpperCase() + "%";
            Predicate title = criteriaBuilder.like(criteriaBuilder.upper(book.get(Book_.title)), findLike);
            Predicate authors = criteriaBuilder.like(criteriaBuilder.upper(book.get(Book_.authors)), findLike);
            Predicate publisher = criteriaBuilder.like(criteriaBuilder.upper(book.get(Book_.publisher)), findLike);

            conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.or(title, authors, publisher));
        }

        criteriaQuery.where(conjunction);
        TypedQuery<Book> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
