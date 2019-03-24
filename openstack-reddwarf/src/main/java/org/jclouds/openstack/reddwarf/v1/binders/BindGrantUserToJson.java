/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.reddwarf.v1.binders;

import java.util.Map;
import java.util.Set;

import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.reddwarf.v1.domain.Database;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * @author Zack Shoylev
 */
public class BindGrantUserToJson implements MapBinder {
    
    @Inject
    private BindToJsonPayload jsonBinder;
    
    @SuppressWarnings("unchecked")
    @Override    
    public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
       Set<Database> databases = Sets.newHashSet();
       if( postParams.get("databaseName")!=null ) {
          Database database = Database.builder().name((String)postParams.get("databaseName")).build();
          databases.add(database);
       }
       else if( postParams.get("databases")!=null ) {
          databases = (Set<Database>) postParams.get("databases");
       }
       return jsonBinder.bindToRequest(request, ImmutableMap.of("databases", databases));
    }

    @Override
    public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
       throw new IllegalStateException("Grant user is a PUT operation");
    }    
}
