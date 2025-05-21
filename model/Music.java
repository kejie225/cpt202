package com.cpt202.music_management.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "songs")
public class Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String album;
    @Lob  // 将字段标记为大型对象(LONGTEXT类型)
    @Column(columnDefinition = "LONGTEXT")
    private String lyrics;
    private String type;
    private String resourceUrl;
    private String coverUrl;
    private String duration;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "singer_id")
    private Singer singer;

    private String status; // 审核状态：Pending / Approved / Rejected
    private String reason; // 审核失败原因（选填）
}
