package cn.com.sae.model.novel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.com.sae.model.CachableObject;

@Entity
@Table(name = "sections")
public class Section extends CachableObject {
	private static final long serialVersionUID = -1911020462461950224L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int id;

	@Column(name = "book_id")
	public int book_id;

	@Column(name = "name")
	public String title;

	@Column(name = "source")
	public String from;

	@Column(name = "text")
	public String text;

	@Column(name = "url")
	public String url;

	@Column(name = "last_update_time")
	private long last_update_time;

	public long getLast_update_time() {
		return new Date().getTime();
	}

	@Column(name = "prev_url")
	public String prevUrl;
	
	@Column(name = "next_url")
	public String nextUrl;

}
