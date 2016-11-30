package be.iqit.vertx.glue.paging

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Created by dvanroeyen on 07/03/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Page<E> {

    List<E> items
    Integer skip
    Integer limit
    Long total

    public Page() {
    }

    public Page(List<E> items, Integer skip, Integer limit, Long total) {
        this.items = items
        this.skip = skip
        this.limit = limit
        this.total = total
    }
}
