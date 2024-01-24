/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.storage.datastore;

import org.keycloak.models.*;
import org.keycloak.models.cache.CacheRealmProvider;
import org.keycloak.models.cache.CacheTenantProvider;
import org.keycloak.models.cache.UserCache;
import org.keycloak.sessions.AuthenticationSessionProvider;
import org.keycloak.storage.*;
import org.keycloak.storage.federated.UserFederatedStorageProvider;

public class LegacyDatastoreProvider implements DatastoreProvider, LegacyStoreManagers {
    private final LegacyDatastoreProviderFactory factory;
    private final KeycloakSession session;

    private AuthenticationSessionProvider authenticationSessionProvider;
    private ClientProvider clientProvider;
    private ClientScopeProvider clientScopeProvider;
    private GroupProvider groupProvider;
    private UserLoginFailureProvider userLoginFailureProvider;
    private RealmProvider realmProvider;
    private TenantProvider tenantProvider;
    private RoleProvider roleProvider;
    private SingleUseObjectProvider singleUseObjectProvider;
    private UserProvider userProvider;
    private UserSessionProvider userSessionProvider;

    private TenantStorageManager tenantStorageManager;
    private ClientScopeStorageManager clientScopeStorageManager;
    private RoleStorageManager roleStorageManager;
    private GroupStorageManager groupStorageManager;
    private ClientStorageManager clientStorageManager;
    private UserProvider userStorageManager;
    private UserFederatedStorageProvider userFederatedStorageProvider;

    public LegacyDatastoreProvider(LegacyDatastoreProviderFactory factory, KeycloakSession session) {
        this.factory = factory;
        this.session = session;
    }

    @Override
    public void close() {
    }

    public ClientProvider clientStorageManager() {
        if (clientStorageManager == null) {
            clientStorageManager = new ClientStorageManager(session, factory.getClientStorageProviderTimeout());
        }
        return clientStorageManager;
    }

    public ClientScopeProvider clientScopeStorageManager() {
        if (clientScopeStorageManager == null) {
            clientScopeStorageManager = new ClientScopeStorageManager(session);
        }
        return clientScopeStorageManager;
    }

    public RoleProvider roleStorageManager() {
        if (roleStorageManager == null) {
            roleStorageManager = new RoleStorageManager(session, factory.getRoleStorageProviderTimeout());
        }
        return roleStorageManager;
    }

    public TenantProvider tenantStorageManager() {
        if (tenantStorageManager == null) {
            tenantStorageManager = new TenantStorageManager(session);
        }
        return tenantStorageManager;
    }

    public GroupProvider groupStorageManager() {
        if (groupStorageManager == null) {
            groupStorageManager = new GroupStorageManager(session);
        }
        return groupStorageManager;
    }

    public UserProvider userStorageManager() {
        if (userStorageManager == null) {
            userStorageManager = new UserStorageManager(session);
        }
        return userStorageManager;
    }

    @Override
    public UserProvider userLocalStorage() {
        return session.getProvider(UserProvider.class);
    }

    @Override
    public UserFederatedStorageProvider userFederatedStorage() {
        if (userFederatedStorageProvider == null) {
            userFederatedStorageProvider = session.getProvider(UserFederatedStorageProvider.class);
        }
        return userFederatedStorageProvider;
    }

    private ClientProvider getClientProvider() {
        // TODO: Extract ClientProvider from CacheRealmProvider and use that instead
        ClientProvider cache = session.getProvider(CacheRealmProvider.class);
        if (cache != null) {
            return cache;
        } else {
            return clientStorageManager();
        }
    }

    private ClientScopeProvider getClientScopeProvider() {
        // TODO: Extract ClientScopeProvider from CacheRealmProvider and use that instead
        ClientScopeProvider cache = session.getProvider(CacheRealmProvider.class);
        if (cache != null) {
            return cache;
        } else {
            return clientScopeStorageManager();
        }
    }

    private TenantProvider getTenantProvider() {
        TenantProvider cache = session.getProvider(CacheTenantProvider.class);
        if (cache != null) {
            return cache;
        } else {
            return tenantStorageManager();
        }
    }

    private GroupProvider getGroupProvider() {
        // TODO: Extract GroupProvider from CacheRealmProvider and use that instead
        GroupProvider cache = session.getProvider(CacheRealmProvider.class);
        if (cache != null) {
            return cache;
        } else {
            return groupStorageManager();
        }
    }

    private RealmProvider getRealmProvider() {
        CacheRealmProvider cache = session.getProvider(CacheRealmProvider.class);
        if (cache != null) {
            return cache;
        } else {
            return session.getProvider(RealmProvider.class);
        }
    }

    private RoleProvider getRoleProvider() {
        // TODO: Extract RoleProvider from CacheRealmProvider and use that instead
        RoleProvider cache = session.getProvider(CacheRealmProvider.class);
        if (cache != null) {
            return cache;
        } else {
            return roleStorageManager();
        }
    }

    private UserProvider getUserProvider() {
        UserCache cache = session.getProvider(UserCache.class);
        if (cache != null) {
            return cache;
        } else {
            return userStorageManager();
        }
    }

    @Override
    public AuthenticationSessionProvider authSessions() {
        if (authenticationSessionProvider == null) {
            authenticationSessionProvider = session.getProvider(AuthenticationSessionProvider.class);
        }
        return authenticationSessionProvider;
    }

    @Override
    public ClientProvider clients() {
        if (clientProvider == null) {
            clientProvider = getClientProvider();
        }
        return clientProvider;
    }

    @Override
    public ClientScopeProvider clientScopes() {
        if (clientScopeProvider == null) {
            clientScopeProvider = getClientScopeProvider();
        }
        return clientScopeProvider;
    }

    @Override
    public TenantProvider tenants() {
        if (tenantProvider == null) {
            tenantProvider = getTenantProvider();
        }
        return tenantProvider;
    }

    @Override
    public GroupProvider groups() {
        if (groupProvider == null) {
            groupProvider = getGroupProvider();
        }
        return groupProvider;
    }

    @Override
    public UserLoginFailureProvider loginFailures() {
        if (userLoginFailureProvider == null) {
            userLoginFailureProvider = session.getProvider(UserLoginFailureProvider.class);
        }
        return userLoginFailureProvider;
    }

    @Override
    public RealmProvider realms() {
        if (realmProvider == null) {
            realmProvider = getRealmProvider();
        }
        return realmProvider;
    }

    @Override
    public RoleProvider roles() {
        if (roleProvider == null) {
            roleProvider = getRoleProvider();
        }
        return roleProvider;
    }

    @Override
    public SingleUseObjectProvider singleUseObjects() {
        if (singleUseObjectProvider == null) {
            singleUseObjectProvider = session.getProvider(SingleUseObjectProvider.class);
        }
        return singleUseObjectProvider;
    }

    @Override
    public UserProvider users() {
        if (userProvider == null) {
            userProvider = getUserProvider();
        }
        return userProvider;
    }

    @Override
    public UserSessionProvider userSessions() {
        if (userSessionProvider == null) {
            userSessionProvider = session.getProvider(UserSessionProvider.class);
        }
        return userSessionProvider;
    }

    @Override
    public ExportImportManager getExportImportManager() {
        return new LegacyExportImportManager(session);
    }

    public MigrationManager getMigrationManager() {
        return new LegacyMigrationManager(session);
    }

}
