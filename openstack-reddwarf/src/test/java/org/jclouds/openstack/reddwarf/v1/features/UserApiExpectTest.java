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
package org.jclouds.openstack.reddwarf.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.reddwarf.v1.domain.Database;
import org.jclouds.openstack.reddwarf.v1.domain.User;
import org.jclouds.openstack.reddwarf.v1.internal.BaseRedDwarfApiExpectTest;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ImmutableSortedSet.Builder;

/**
 * Tests UserApi Guice wiring and parsing
 *
 * @author Zack Shoylev
 */
@Test(groups = "unit", testName = "UserApiExpectTest")
public class UserApiExpectTest extends BaseRedDwarfApiExpectTest {

   public void testCreateUserSimple() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("POST")
            .payload(payloadFromResourceWithContentType("/user_create_simple_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(202).build() // response
            ).getUserApiForZone("RegionOne");

      boolean result = api.create("instanceId-1234-5678", "dbuser1", "password", "databaseA");
      assertTrue(result);
   }

   public void testCreateUserSimpleFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("POST")
            .payload(payloadFromResourceWithContentType("/user_create_simple_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(404).build() // response
            ).getUserApiForZone("RegionOne");

      boolean result = api.create("instanceId-1234-5678", "dbuser1", "password", "databaseA");
      assertFalse(result);
   }

   public void testCreateUser() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("POST")
            .payload(payloadFromResourceWithContentType("/user_create_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(202).build() // response
            ).getUserApiForZone("RegionOne");

      Set<Database> databases1 = Sets.newHashSet();
      databases1.add( Database.builder().name("databaseA").build() );      
      Builder<Database> databases2builder = ImmutableSortedSet.<Database>naturalOrder();
      databases2builder.add( Database.builder().name("databaseB").build() );
      databases2builder.add( Database.builder().name("databaseC").build() );
      Set<Database> databases2 = databases2builder.build();
      Set<Database> databases3 = Sets.newHashSet();
      databases3.add( Database.builder().name("databaseD").build() );
      User user1 = User.builder().databases( databases1 ).name("dbuser1").password("password").build();
      User user2 = User.builder().databases( databases2 ).name("dbuser2").password("password").build();
      User user3 = User.builder().databases( databases3 ).name("dbuser3").password("password").build();
      Set<User> users = Sets.newHashSet();
      users.add(user1);
      users.add(user2);
      users.add(user3);
      
      boolean result = api.create("instanceId-1234-5678", ImmutableSortedSet.<User>naturalOrder().addAll(users).build());
      assertTrue(result);
   }

   public void testCreateUserFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("POST")
            .payload(payloadFromResourceWithContentType("/user_create_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(404).build() // response
            ).getUserApiForZone("RegionOne");

      Set<Database> databases1 = Sets.newHashSet();
      databases1.add( Database.builder().name("databaseA").build() );
      Builder<Database> databases2builder = ImmutableSortedSet.<Database>naturalOrder();
      databases2builder.add( Database.builder().name("databaseB").build() );
      databases2builder.add( Database.builder().name("databaseC").build() );
      Set<Database> databases2 = databases2builder.build();
      Set<Database> databases3 = Sets.newHashSet();
      databases3.add( Database.builder().name("databaseD").build() );
      User user1 = User.builder().databases( databases1 ).name("dbuser1").password("password").build();
      User user2 = User.builder().databases( databases2 ).name("dbuser2").password("password").build();
      User user3 = User.builder().databases( databases3 ).name("dbuser3").password("password").build();
      Set<User> users = Sets.newHashSet();
      users.add(user1);
      users.add(user2);
      users.add(user3);
      
      boolean result = api.create("instanceId-1234-5678",  ImmutableSortedSet.<User>naturalOrder().addAll(users).build());
      assertFalse(result);
   }

   public void testGrantUserSimple() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1/databases");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("PUT")
            .payload(payloadFromResourceWithContentType("/user_grant_simple_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(202).build() // response
            ).getUserApiForZone("RegionOne");

      boolean result = api.grant("instanceId-1234-5678", "dbuser1", "databaseZ");
      assertTrue(result);
   }

   public void testGrantUserSimpleFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1/databases");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("PUT")
            .payload(payloadFromResourceWithContentType("/user_grant_simple_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(404).build() // response
            ).getUserApiForZone("RegionOne");

      boolean result = api.grant("instanceId-1234-5678", "dbuser1", "databaseZ");
      assertFalse(result);
   }

   public void testGrantUser() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1/databases");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("PUT")
            .payload(payloadFromResourceWithContentType("/user_grant_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(202).build() // response
            ).getUserApiForZone("RegionOne");

      Builder<Database> databasesBuilder = ImmutableSortedSet.<Database>naturalOrder();
      databasesBuilder.add( Database.builder().name("databaseC").build() );
      databasesBuilder.add( Database.builder().name("databaseD").build() );
      Set<Database> databases = databasesBuilder.build();
      
      boolean result = api.grant("instanceId-1234-5678", "dbuser1", databases);
      assertTrue(result);
   }

   public void testGrantUserFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1/databases");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("PUT")
            .payload(payloadFromResourceWithContentType("/user_grant_request.json", MediaType.APPLICATION_JSON))
            .build(),
            HttpResponse.builder().statusCode(404).build() // response
            ).getUserApiForZone("RegionOne");

      Builder<Database> databasesBuilder = ImmutableSortedSet.<Database>naturalOrder();
      databasesBuilder.add( Database.builder().name("databaseC").build() );
      databasesBuilder.add( Database.builder().name("databaseD").build() );
      Set<Database> databases = databasesBuilder.build();
      
      boolean result = api.grant("instanceId-1234-5678", "dbuser1", databases);
      assertFalse(result);
   }
   
   public void testRevokeUser() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1/databases/databaseA");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("DELETE")
            .build(),
            HttpResponse.builder().statusCode(202).build() // response
            ).getUserApiForZone("RegionOne");

      Set<Database> databases = Sets.newHashSet();
      databases.add( Database.builder().name("databaseC").build() );
      databases.add( Database.builder().name("databaseD").build() );
      boolean result = api.revoke("instanceId-1234-5678", "dbuser1", "databaseA");
      assertTrue(result);
   }
   
   public void testRevokeUserFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1/databases/databaseA");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("DELETE")
            .build(),
            HttpResponse.builder().statusCode(404).build() // response
            ).getUserApiForZone("RegionOne");

      Set<Database> databases = Sets.newHashSet();
      databases.add( Database.builder().name("databaseC").build() );
      databases.add( Database.builder().name("databaseD").build() );
      boolean result = api.revoke("instanceId-1234-5678", "dbuser1", "databaseA");
      assertFalse(result);
   }
   
   public void testDeleteUser() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("DELETE")
            .build(),
            HttpResponse.builder().statusCode(202).build() // response
            ).getUserApiForZone("RegionOne");

      Set<Database> databases = Sets.newHashSet();
      databases.add( Database.builder().name("databaseC").build() );
      databases.add( Database.builder().name("databaseD").build() );
      boolean result = api.delete("instanceId-1234-5678", "dbuser1");
      assertTrue(result);
   }
   
   public void testDeleteUserFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint) // bad naming convention, you should not be able to change the method to POST
            .method("DELETE")
            .build(),
            HttpResponse.builder().statusCode(404).build() // response
            ).getUserApiForZone("RegionOne");

      Set<Database> databases = Sets.newHashSet();
      databases.add( Database.builder().name("databaseC").build() );
      databases.add( Database.builder().name("databaseD").build() );
      boolean result = api.delete("instanceId-1234-5678", "dbuser1");
      assertFalse(result);
   }
   
   public void testListUsers() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/reddwarf_user_list.json")).build()
      ).getUserApiForZone("RegionOne");

      Set<User> users = api.list("instanceId-1234-5678").toSet();
      assertEquals(users.size(), 4);
      assertEquals(users.iterator().next().getDatabases().size(), 0);
      assertEquals(users.iterator().next().getName(), "dbuser1");
   }
   
   public void testListUsersFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/reddwarf_user_list.json")).build()
      ).getUserApiForZone("RegionOne");

      Set<User> users = api.list("instanceId-1234-5678").toSet();
      assertEquals(users.size(), 0);
   }
   
   public void testUserGetDatabaseList() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1/databases");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/user_list_access.json")).build()
      ).getUserApiForZone("RegionOne");

      Set<Database> databases = api.getDatabaseList("instanceId-1234-5678", "dbuser1").toSet();
      assertEquals(databases.size(), 2);
      assertEquals(databases.iterator().next().getName(), "databaseA");
   }
   
   public void testUserGetDatabaseListFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/dbuser1/databases");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/user_list_access.json")).build()
      ).getUserApiForZone("RegionOne");

      Set<Database> databases = api.getDatabaseList("instanceId-1234-5678", "dbuser1").toSet();
      assertEquals(databases.size(), 0);
   }
   
   public void testGetUser() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/exampleuser");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/user_get.json")).build()
      ).getUserApiForZone("RegionOne");

      User user = api.get("instanceId-1234-5678", "exampleuser");
      assertEquals(user.getName(), "exampleuser");
      assertEquals(user.getDatabases().size(), 2);
      assertEquals(user.getDatabases().iterator().next().getName(), "databaseA");
   }
   
   public void testGetUserFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/instances/instanceId-1234-5678/users/exampleuser");
      UserApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/user_get.json")).build()
      ).getUserApiForZone("RegionOne");

      User user = api.get("instanceId-1234-5678", "exampleuser");
      assertNull(user);
   }
}
