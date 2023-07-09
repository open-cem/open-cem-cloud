import { IdTokenClaims, User } from "oidc-client-ts";
import { KeycloakAppUser } from "./AppUser";

describe('App user roles', () => {
    const mockUser = (roles: string[]) =>
        new KeycloakAppUser({
            profile: {
                [KeycloakAppUser.realmAccessClaimName]: {
                    [KeycloakAppUser.rolesClaimName]: roles
                },
                sub: "",
                iss: "",
                aud: "",
                exp: 0,
                iat: 0
            } as IdTokenClaims
        } as User);

    it('should not be empty', () => {
        const user = mockUser(["Administrator", "Test"]);

        const roles = user.roles;

        expect(roles).toHaveLength(2);
    });

    it('isAdministrator should be true', () => {
        const user = mockUser(["SomeRole", "Administrator"]);

        const isAdministrator = user.isAdministrator;

        expect(isAdministrator).toBeTruthy();
    });

    it('isAdministrator should be false', () => {
        const user = mockUser(["SomeRole"]);

        const isAdministrator = user.isAdministrator;

        expect(isAdministrator).toBeFalsy();
    });

    it('isAdministrator should be false when empty roles', () => {
        const user = mockUser([]);

        const isAdministrator = user.isAdministrator;

        expect(isAdministrator).toBeFalsy();

    });

    it('isInstallateur should be true', () => {
        const user = mockUser(["SomeRole", "Installateur", "Administrator"]);

        const isInstallateur = user.isInstallateur;

        expect(isInstallateur).toBeTruthy();
    });

    it('isInstallateur should be false', () => {
        const user = mockUser(["SomeRole", "Administrator"]);

        const isInstallateur = user.isInstallateur;

        expect(isInstallateur).toBeFalsy();
    });

    it('isInstallateur should be false when empty roles', () => {
        const user = mockUser([]);

        const isInstallateur = user.isInstallateur;

        expect(isInstallateur).toBeFalsy();
    });
});