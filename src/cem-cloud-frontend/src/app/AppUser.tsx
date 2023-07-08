import { User } from "oidc-client-ts";
import _ from "lodash";

interface AppUser {
    get roles() : string[];
    get isAdministrator() : boolean;
    get isInstallateur() : boolean;
}

class KeycloakAppUser implements AppUser {
    private readonly realmAccessClaimName = "realm_access";
    private readonly rolesClaimName = "roles";
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
        const realmAccess = this.user.profile[this.realmAccessClaimName] as any;
        return realmAccess[this.rolesClaimName] as string[];
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