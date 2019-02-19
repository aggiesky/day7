package edu.acc.j2ee.hubbub;

public class Pager implements java.io.Serializable {
    private int pageSize;
    private int page;
    private int size;
    
    public static Pager of(String page, int pageSize, int size) {
        Pager pager = new Pager();
        try {
            pager.setPage(Integer.parseInt(page));
        }
        catch (NullPointerException | NumberFormatException ne) {}
        pager.setPageSize(pageSize);
        pager.setSize(size);
        return pager;
    }

    public Pager() {
    }
    
    public Pager(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize < 1)
            throw new IllegalArgumentException("Page size cannot be negative");
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    @Override
    public String toString() {
        return String.format("Pager[pageSize=%d, page=%d, size=%d, last=%d]",
                pageSize, page, size, getLast());
    }

    public int getLast() {
        int result = size / pageSize; // almost always the right answer
        if (size % pageSize == 0) // 10 % 5 = 0, 10 / 5 =2, but we should be on page 1
            result--;
        return result;

    }

    public boolean getHasNext() {
        return page < getLast();
    }
    
    public boolean getHasPrev() {
        return page > 0;
    }
    
    public int getNext() {
        return getHasNext() ? page + 1 : 0;
    }
    
    public int getPrev() {
        return getHasPrev() ? page - 1 : 0;
    }
}
