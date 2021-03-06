package pro.bolshakov.geekbrains.lesson4.domain;

import javax.persistence.*;

@Entity
@Table(name = "category_holder_persist")
public class CategoryHolderCascadePersist {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category_id")
    private Category category;

    public CategoryHolderCascadePersist() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
