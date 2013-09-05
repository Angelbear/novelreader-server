package cn.com.sae.model.novel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Subselect;

import cn.com.sae.annotation.MustFilterField;
import cn.com.sae.model.CachableObject;

@Entity
@Subselect("SELECT id, book_id, name, source, url, last_update_time, prev_url, next_url FROM sections")
public class SectionView extends CachableObject {

	private static final long serialVersionUID = 678427197568076436L;

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

	@Column(name = "url")
	public String url;

	@Column(name = "last_update_time")
	private long last_update_time;

	@Column(name = "prev_url")
	public String prevUrl;
	
	@Column(name = "next_url")
	public String nextUrl;

}
