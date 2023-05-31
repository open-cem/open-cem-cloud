import { AuthContextProps } from "react-oidc-context";

export class Installation {
    id: string;
    name: string;
    serialNumber: number;

    constructor(id: string, name: string, serialNumber: number) {
        this.id = id;
        this.name = name;
        this.serialNumber = serialNumber;
    }
}

export class InstallationService {
    private apiUri: string;
    private auth: AuthContextProps;

    constructor(apiUri: string, auth: AuthContextProps) {
        this.apiUri = apiUri;
        this.auth = auth;
    }

    public loadInstallations = () => {
        return fetch(`${this.apiUri}installations`,
            {
                headers: [["authorization", `Bearer ${this.auth.user?.access_token}`]]
            })
            .then(r => r.json())
            .then((installations: Installation[]) => installations)
    };
}