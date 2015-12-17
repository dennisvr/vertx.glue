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

import com.mongodb.async.client.MongoCollection

import org.bson.Document
import org.bson.conversions.Bson
import rx.Observable

/**
 * Created by dvanroeyen on 06/12/15.
 */
class DefaultMongoRepository implements MongoRepository {

    MongoCollection<Document> collection

    public DefaultMongoRepository(MongoCollection collection ) {
        this.collection = collection
    }

    public Observable<Document> find() {
        MongoUtil.asObservable(collection.find())
    }

    public Observable<Document> find(Bson filter) {
        MongoUtil.asObservable(collection.find(filter))
    }

    public Observable<Void> save(Document document) {
        ObservableSingleResultCallback<Document> callback = new ObservableSingleResultCallback<>()
        collection.insertOne(document, callback)
        return callback.asObservable()
    }


}
