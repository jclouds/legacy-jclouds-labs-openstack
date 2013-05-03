/*
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
package org.jclouds.openstack.reddwarf.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import java.beans.ConstructorProperties;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * An Openstack Reddwarf Database.
 * 
 * @author Zack Shoylev
 */
public class Database implements Comparable<Database>{
    
    private final String name;
    
    @ConstructorProperties({
        "name"
    })
    protected Database(String name) {
        this.name = checkNotNull(name, "name required");
    }    
    
    /**
     * @return the name of this database
     * @see Database.Builder#name(String)
     */
    public String getName() {
        return this.name;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Database that = Database.class.cast(obj);
        return Objects.equal(this.name, that.name);
    }

    protected ToStringHelper string() {
        return Objects.toStringHelper(this)
                .add("name", name);
    }

    @Override
    public String toString() {
        return string().toString();
    }

    public static Builder builder() { 
        return new Builder();
    }

    public Builder toBuilder() { 
        return new Builder().fromDatabase(this);
    }
    
    public static class Builder {
        protected String name;
        
        /** 
         * @param name The name of this database
         * @return The builder object
         * @see Database#getName()
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 
         * @return A new Database object
         */
        public Database build() {
            return new Database(name);
        }

        public Builder fromDatabase(Database in) {
            return this
                    .name(in.getName());
        }        
    }
    
    @Override
    public int compareTo(Database that) {
        return this.getName().compareTo(that.getName());
    }   
}
