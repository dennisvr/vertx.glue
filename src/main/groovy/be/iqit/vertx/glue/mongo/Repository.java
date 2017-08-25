/**
 * Copyright 2015 Dennis Van Roeyen, iQit, BVBA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package be.iqit.vertx.glue.mongo;

import be.iqit.vertx.glue.paging.Page;
import org.bson.Document;
import org.bson.conversions.Bson;
import rx.Observable;

import java.util.List;

/**
 * Created by dvanroeyen on 06/12/15.
 */
public interface Repository<E> {

    Observable<E> find();

    Observable<E> find(Bson filter);

    Observable<Long> count(Bson filter);

    Observable<Long> delete(Bson filter);

    Observable<Page<E>> page(Bson filter, Integer offset, Integer limit);

    Observable<E> save(Bson filter, E document);

    <A> Observable<List<A>> aggregate(Class<A> clazz, Bson... pipeline);
}
