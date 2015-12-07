package be.iqit.vertx.sample.service;

import be.iqit.vertx.sample.domain.Invoice;
import rx.Observable;

/**
 * Created by dvanroeyen on 06/12/15.
 */
public interface InvoiceService {

    Observable<Invoice> saveInvoice(Invoice invoice);
}
