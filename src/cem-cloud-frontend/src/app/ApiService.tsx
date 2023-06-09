import { getApiUri } from "./AppSettings";

class ApiService {
    protected apiBuilder<ResponseType>() {
        return new ApiBuilder<ResponseType>();
    }
}

class ApiBuilder<ResultType> {
    private uri: string = "";
    private headers: [string, string][] = [];

    public withUri(uri: string) {
        this.uri = uri;
        return this;
    }

    public withAuthorization(accessToken: string) {
        if (!this.headers.find(h => h[0] === "authorization")) {
            this.headers.push(this.createAuthorizationHeader(accessToken));
        }

        return this;
    }

    public fetch() {
        const apiUri = getApiUri();
        return fetch(`${apiUri}${this.uri}`,
            {
                headers: this.headers
            })
            .then(r => r.json())
            .then((obj: ResultType) => obj);
    }

    private createAuthorizationHeader(accessToken: string): [string, string] {
        return ["authorization", `Bearer ${accessToken}`];
    }
}

export { ApiService, ApiBuilder };