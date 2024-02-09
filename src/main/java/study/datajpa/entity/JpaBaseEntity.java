package study.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;


    /*
    JPA 주요 이벤트 어노테이션
    @PrePersist, @PostPersist
    @PreUpdate, @PostUpdate
    * */
    @PrePersist
    public void PrePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate
    public void PreUpdate() {
        updatedDate = LocalDateTime.now();
    }

}
