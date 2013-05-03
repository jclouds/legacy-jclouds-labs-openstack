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

import static com.google.common.base.Preconditions.checkArgument;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jclouds.openstack.reddwarf.v1.domain.Database;
import org.jclouds.openstack.reddwarf.v1.domain.Instance;
import org.jclouds.openstack.reddwarf.v1.domain.User;
import org.jclouds.openstack.reddwarf.v1.internal.BaseRedDwarfApiLiveTest;
import org.jclouds.openstack.reddwarf.v1.predicates.InstancePredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Zack Shoylev
 */
@Test(groups = "live", testName = "UserApiLiveTest")
public class UserApiLiveTest extends BaseRedDwarfApiLiveTest {

   // zone to instance
   private static Map<String,List<Instance>> instancesToDelete = Maps.newHashMap();
   // not deleting users. they will be deleted when instances are deleted

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      for (String zone : api.getConfiguredZones()) {
         // create instances
         List<Instance> instanceList = Lists.newArrayList();
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         Instance first = instanceApi.create("1", 1, "first_user_testing");
         Instance second = instanceApi.create("1", 1, "second_user_testing");
         instanceList.add(first);
         instanceList.add(second);
         InstancePredicates.awaitAvailable(instanceApi).apply(first);
         InstancePredicates.awaitAvailable(instanceApi).apply(second);        
         instancesToDelete.put(zone, instanceList);
         // create users
         User user1 = User.builder()
               .name("user1")
               .password(UUID.randomUUID().toString())
               .databases(ImmutableSet.of(
                     Database.builder().name("u1db1").build(), 
                     Database.builder().name("u1db1").build())).build();
         User user2 = User.builder()
               .name("user2")
               .password(UUID.randomUUID().toString())
               .databases(ImmutableSet.of(
                     Database.builder().name("u2db1").build(), 
                     Database.builder().name("u2db1").build())).build();
         User user3 = User.builder()
               .name("user3")
               .password(UUID.randomUUID().toString())
               .databases(ImmutableSet.of(
                     Database.builder().name("u3db1").build(), 
                     Database.builder().name("u3db1").build())).build();
         UserApi userApi = api.getUserApiForZone(zone);
         userApi.create(first.getId(), ImmutableSet.of(user1, user2));
         userApi.create(second.getId(), ImmutableSet.of(user3));
      }
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   public void tearDown(){
      for (String zone : api.getConfiguredZones()) {
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         for(Instance instance : instancesToDelete.get(zone)){
            if( !instanceApi.delete(instance.getId() ) )
               throw new RuntimeException("Could not delete a database instance after tests!");
         }
      }
      super.tearDown();
   }

   private void checkUser(User user) {
      assertNotNull(user.getName(), "Name cannot be null for " + user);
      checkArgument(user.getDatabases().size() > 0, "Number of databases must not be 0");
   }

   @Test
   public void testListUsers() {
      for (String zone : api.getConfiguredZones()) {
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         UserApi userApi = api.getUserApiForZone(zone);
         assertTrue(instanceApi.list().size() >= 2);
         for(Instance instance : instanceApi.list() ) {
            if(!instance.getName().contains("user_testing"))continue;
            assertTrue(userApi.list(instance.getId()).size() >=1);
            for(User user : userApi.list(instance.getId())){
               checkUser(user);      
            }
         }  
      }   
   }    

   @Test
   public void testGetUser() {
      for (String zone : api.getConfiguredZones()) {
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         UserApi userApi = api.getUserApiForZone(zone);
         assertTrue(instanceApi.list().size() >= 2);
         for(Instance instance : instanceApi.list() ) {
            if(!instance.getName().contains("user_testing"))continue;
            assertTrue(userApi.list(instance.getId()).size() >=1);
            for(User user : userApi.list(instance.getId())){
               User userFromGet = userApi.get(instance.getId(), user.getName());
               assertEquals(userFromGet.getName(), user.getName());
               assertEquals(userFromGet.getDatabases(), user.getDatabases());     
            }
         }  
      } 
   }

   @Test
   public void testGetDatabaseListForUser() {
      for (String zone : api.getConfiguredZones()) {
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         UserApi userApi = api.getUserApiForZone(zone);
         assertTrue(instanceApi.list().size() >= 2 );
         for(Instance instance : instanceApi.list() ) {
            if(!instance.getName().contains("user_testing"))continue;
            assertTrue(userApi.list(instance.getId()).size() >=1);
            for(User user : userApi.list(instance.getId())){
               assertTrue(userApi.getDatabaseList(instance.getId(), user.getName()).size()>0);
            }
         }  
      } 
   }

   @Test
   public void testGrantAndRevokeAcccessForUser() {
      for (String zone : api.getConfiguredZones()) {
         InstanceApi instanceApi = api.getInstanceApiForZone(zone);
         UserApi userApi = api.getUserApiForZone(zone);
         assertTrue(instanceApi.list().size() >= 2);
         for(Instance instance : instanceApi.list() ) {
            if(!instance.getName().contains("user_testing"))continue;
            assertTrue(userApi.list(instance.getId()).size() >=1);
            for(User user : userApi.list(instance.getId())){
               userApi.grant(instance.getId(), user.getName(), "dbA");
               userApi.grant(instance.getId(), user.getName(), ImmutableSet.of(
                     Database.builder().name("dbB").build(), 
                     Database.builder().name("dbC").build()));

               Set<Database> databases = userApi.getDatabaseList(instance.getId(), user.getName()).toSet();
               assertTrue(databases.contains(Database.builder().name("dbA").build()));
               assertTrue(databases.contains(Database.builder().name("dbB").build()));
               assertTrue(databases.contains(Database.builder().name("dbC").build()));

               userApi.revoke(instance.getId(), user.getName(), "dbA");
               userApi.revoke(instance.getId(), user.getName(), "dbB");
               userApi.revoke(instance.getId(), user.getName(), "dbC");

               databases = userApi.getDatabaseList(instance.getId(), user.getName()).toSet();
               assertFalse(databases.contains(Database.builder().name("dbA").build()));
               assertFalse(databases.contains(Database.builder().name("dbB").build()));
               assertFalse(databases.contains(Database.builder().name("dbC").build()));
            }
         }  
      } 
   }

   @Test
   public void testGetUserWhenNotFound() {
      for (String zone : api.getConfiguredZones()) {
         UserApi userApi = api.getUserApiForZone(zone);
         assertNull(userApi.get(api.getInstanceApiForZone(zone).list().iterator().next().getId(), "9999"));
      }
   }
}
