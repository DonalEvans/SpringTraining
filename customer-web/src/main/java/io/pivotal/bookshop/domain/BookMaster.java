package io.pivotal.bookshop.domain;

import java.io.Serializable;

public class BookMaster implements Serializable
{

    private int itemNumber;
    private String description;
    private int yearPublished;
    private String author;
    private String title;
    private boolean checkedOut;
    private int currentOwner;

    public BookMaster(int itemNumber, String description,
                      int yearPublished, String author, String title)
    {
        super();
        this.itemNumber = itemNumber;
        this.description = description;
        this.yearPublished = yearPublished;
        this.author = author;
        this.title = title;
        this.checkedOut = false;
        this.currentOwner = 0;
    }

    public BookMaster() {}

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + itemNumber;
        return result;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BookMaster other = (BookMaster) obj;
        if (itemNumber != other.itemNumber || ! this.author.equals(other.author) || ! this.title.equals(other.title))
            return false;
        return true;
    }


    public int getItemNumber()
    {
        return itemNumber;
    }
    public void setItemNumber(int itemNumber)
    {
        this.itemNumber = itemNumber;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public int getYearPublished()
    {
        return yearPublished;
    }
    public void setYearPublished(int yearPublished)
    {
        this.yearPublished = yearPublished;
    }
    public String getAuthor()
    {
        return author;
    }
    public void setAuthor(String author)
    {
        this.author = author;
    }
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }

    public boolean isCheckedOut() {
        return checkedOut;
    }

    public void setCheckedOut(boolean checkedOut) {
        this.checkedOut = checkedOut;
    }
    public int getCurrentOwner() {
        return this.currentOwner;
    }
    public void setCurrentOwner(int currentOwner) {
        this.currentOwner = currentOwner;
    }


    @Override
    public String toString()
    {
        return "BookMaster [itemNumber=" + itemNumber + ", title=" + title
                + "]";
    }}
