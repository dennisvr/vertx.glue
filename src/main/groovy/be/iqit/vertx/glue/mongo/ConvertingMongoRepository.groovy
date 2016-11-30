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
package be.iqit.vertx.glue.mongo

import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.glue.paging.Page
import com.mongodb.rx.client.MongoCollection
import groovy.transform.TypeChecked
import org.bson.conversions.Bson
import rx.Observable
import rx.functions.Func1

/**
 * Created by dvanroeyen on 06/12/15.
 */
@TypeChecked
class ConvertingMongoRepository<E> implements Repository<E> {

    MongoCollection collection
    Converter converter
    Class<E> clazz

    public ConvertingMongoRepository(Class<E> clazz, MongoCollection collection, Converter converter) {
        this.clazz = clazz
        this.collection = collection
        this.converter = converter
    }

    public Observable<E> find() {
        collection
                .find()
                .toObservable()
                .flatMap({document -> converter.convert(document, clazz)} as Func1)
    }

    public Observable<E> find(Bson filter) {
        return collection
                .find(filter)
                .toObservable()
                .flatMap({document -> converter.convert(document, clazz)} as Func1)
    }

    @Override
    Observable<Long> count(Bson filter) {
        return collection.count(filter)
    }

    @Override
    Observable<Page<E>> page(Bson filter, Integer skip, Integer limit) {
        collection.count(filter).flatMap({ Long total ->
            return collection
                    .find(filter)
                    .skip(skip?:0)
                    .limit(limit?:0)
                    .toObservable()
                    .flatMap({document -> converter.convert(document, clazz)} as Func1)
                    .toList()
                    .map({ List<E> items ->
                return new Page<E>(items, skip, limit, total)
            } as Func1)
        })
    }

    public Observable<Void> save(E object) {
        converter.convert(object, collection.getDocumentClass()).flatMap({ document ->
            collection.insertOne(document).asObservable().map({success -> null})
        })

    }

    public <A> Observable<List<A>> aggregate(Class<A> clazz, Bson... pipeline) {
        collection.aggregate(Arrays.asList(pipeline), clazz).toObservable().toList()
    }

}
