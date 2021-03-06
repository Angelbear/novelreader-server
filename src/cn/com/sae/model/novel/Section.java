package cn.com.sae.model.novel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import cn.com.sae.annotation.MustFilterField;
import cn.com.sae.model.CachableObject;

@Entity
@Table(name = "sections" , uniqueConstraints = {
		@UniqueConstraint(columnNames = "id"),
		@UniqueConstraint(columnNames = "url") })
public class Section extends CachableObject {
	private static final long serialVersionUID = -1911020462461950224L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int id;

	@Column(name = "book_id")
	public int book_id;

	@Column(name = "name")
	@MustFilterField
	public String title;

	@Column(name = "source")
	public String from;

	@Column(name = "text")
	@MustFilterField
	public String text;

	@Column(name = "url")
	public String url;

	@Column(name = "last_update_time")
	private long last_update_time;

	@Column(name = "prev_url")
	public String prevUrl;
	
	@Column(name = "next_url")
	public String nextUrl;

}
