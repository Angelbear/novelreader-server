package cn.com.sae.model.novel;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import cn.com.sae.annotation.MustFilterField;
import cn.com.sae.model.CachableObject;

@Entity
@Table(name = "books", uniqueConstraints = {
		@UniqueConstraint(columnNames = "id"),
		@UniqueConstraint(columnNames = "url") })
public class Book extends CachableObject {
	private static final long serialVersionUID = 755629434886328157L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int id;

	@Column(name = "name")
	@MustFilterField
	public String name;

	@Column(name = "url")
	public String url;

	@Column(name = "source")
	public String from;

	@Column(name = "cover")
	public String img;

	@Column(name = "description")
	@MustFilterField
	public String description;

	@Column(name = "last_update_time")
	private long last_update_time;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "book_id")
	@OrderBy("id")
	public List<SectionView> sections;
}
