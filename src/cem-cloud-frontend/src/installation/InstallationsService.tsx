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
    private auth: AuthContextProps;

    constructor(auth: AuthContextProps) {
        this.auth = auth;
    }

    public loadInstallations = () => {
        return fetch("http://localhost:8080/api/installations",
            {
                headers: [["authorization", `Bearer ${this.auth.user?.access_token}`]]
            })
            .then(r => r.json())
            .then((installations: Installation[]) => installations)
    };
}