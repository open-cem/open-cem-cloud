import { getApiUri } from "./AppSettings";

class ApiService {
    protected apiBuilder<ResponseType>() {
        return new ApiBuilder<ResponseType>();
    }
}

class ApiBuilder<ResultType> {
    private uri: string = "";
    private headers: [string, string][] = [];
    private method: string = "GET";
    private body?: string;
    private formData?: FormData;

    public withUri(uri: string) {
        this.uri = uri;
        return this;
    }

    public withBody(body: object) {
        this.body = JSON.stringify(body);
        if (!this.headers.find(h => h[0] === "Content-Type")) {
            this.headers.push(["Content-Type", "application/json"])
        }
        return this;
    }

    public withFormData(formData: FormData) {
        this.formData = formData;
        return this;
    }

    public withMethod(method: string) {
        this.method = method;
        return this;
    }

    public post() {
        return this.withMethod('POST');
    }

    public put() {
        return this.withMethod('PUT');
    }

    public withAuthorization(accessToken: string) {
        if (!this.headers.find(h => h[0] === "Authorization")) {
            this.headers.push(this.createAuthorizationHeader(accessToken));
        }

        return this;
    }

    private fetchResponse() {
        const apiUri = getApiUri();
        return fetch(`${apiUri}${this.uri}`,
            {
                method: this.method,
                headers: this.headers,
                body: this.body ?? this.formData,
            });
    }

    public fetch() {
        return this.fetchResponse()
            .then(_ => Promise<void>);
    }

    public fetchBody() {
        return this.fetchResponse()
            .then(r => r.json())
            .then((obj: ResultType) => obj);
    }

    public fetchLocationHeader() {
        return this.fetchResponse()
            .then(r => r.headers.get('Location'));
    }

    public fetchBlobResponse() {
        return this.fetchResponse()
            .then(r => r.blob())
            .then(b => b.size === 0 ? null : URL.createObjectURL(b));
    }

    private createAuthorizationHeader(accessToken: string): [string, string] {
        return ["authorization", `Bearer ${accessToken}`];
    }
}

export { ApiService, ApiBuilder };