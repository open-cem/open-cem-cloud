import { User } from "oidc-client-ts";
import _ from "lodash";

interface AppUser {
    get roles() : string[];
    get isAdministrator() : boolean;
    get isInstallateur() : boolean;
}

class KeycloakAppUser implements AppUser {
    public static readonly realmAccessClaimName = "realm_access";
    public static readonly rolesClaimName = "roles";
    private readonly administratorRole = "Administrator";
    private readonly installatuerRole = "Installateur";
    private readonly user: User;
    private _roles?: string[];

    constructor(user: User) {
        this.user = user;
    }
    get isAdministrator(): boolean {
        return _.includes(this.roles, this.administratorRole);
    }
    get isInstallateur(): boolean {
        return _.includes(this.roles, this.installatuerRole);
    }
    
    public get roles() : string[] {
        return (this._roles ?? (this._roles = this.getRoles()))
    }
    
    private getRoles(): string[] {
        let roles: string[] = [];
        const realmAccess = this.user.profile[KeycloakAppUser.realmAccessClaimName] as any;
        if (realmAccess) {
            roles = realmAccess[KeycloakAppUser.rolesClaimName] as string[];
            if (!roles) {
                roles = [];
                console.warn(`${KeycloakAppUser.realmAccessClaimName}.${KeycloakAppUser.rolesClaimName} claim not present in Id token. Therefore, the roles cannot be determined. Some functionality may be restricted.`);
            }
        } else {
            console.warn(`${KeycloakAppUser.realmAccessClaimName} claim not present in Id token. Therefore, the roles cannot be determined. Some functionality may be restricted.`);
        }

        return roles;
    }
}

class EmptyAppUser implements AppUser {
    get isAdministrator(): boolean {
        return false;
    }
    get isInstallateur(): boolean {
        return false;
    }
    get roles(): string[] {
        return [];
    }
}

export { KeycloakAppUser, EmptyAppUser };
export type { AppUser };