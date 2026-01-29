package com.payment.payment_service.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.payment.payment_service.constants.ModelConstant.CREATED_AT;
import static com.payment.payment_service.constants.ModelConstant.UPDATED_AT;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class BaseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = CREATED_AT, updatable = false)
    @JsonProperty("createdAt")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = UPDATED_AT)
    @JsonProperty("updatedAt")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
