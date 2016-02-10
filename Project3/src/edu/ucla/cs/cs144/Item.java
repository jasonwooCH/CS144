package edu.ucla.cs.cs144;

import java.util.List;
import java.util.ArrayList;

public class Item {

	public Item() {
	}

	public Item(String id,
				String name,
				String description,
				List<String> categories) {

		this.id = id;
		this.name = name;
		this.description = description;
		this.categories = categories;
	}

	private String name;
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private String id;
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}

	private String description;
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	private List<String> categories;
	public List<String> getCategories() {
		return this.categories;
	}
	public void addCategory(String newcat) {
		if (this.categories == null) {
			this.categories = new ArrayList<String>();
			this.categories.add(newcat);
		}
		else
			this.categories.add(newcat);
	}

	public String toString() {
		return "Item " + getId() + ": " + getName() + " (" + getDescription() + ")"; 
	}

}