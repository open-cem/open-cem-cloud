import { getApiUri } from "./AppSettings";
import _ from "lodash";

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
    private parameters: { name: string, value: string }[] = [];

    public withUri(uri: string) {
        this.uri = uri;
        return this;
    }

    public withParameter(name: string, value: string) {
        if (this.parameters.find(p => p.name === name)) {
            throw new Error(`There is already a parameter with the name: ${name}`);
        }

        this.parameters.push({ name: name, value: value });
        return this;
    }

    public withBody(body: object) {
        console.log(body);
        this.body = JSON.stringify(body);
        console.log(this.body);
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

    public delete() {
        return this.withMethod('DELETE');
    }

    public withAuthorization(accessToken: string) {
        if (!this.headers.find(h => h[0] === "Authorization")) {
            this.headers.push(this.createAuthorizationHeader(accessToken));
        }

        return this;
    }

    private fetchResponse() {
        const apiUri = getApiUri();
        let queryString = "";
        if (this.parameters.length) {
            const keyValueString = this.parameters.map(p => `${p.name}=${p.value}`);
            queryString = `?${_.join(keyValueString, "&")}`;
        }
        return fetch(`${apiUri}${this.uri}${queryString}`,
            {
                method: this.method,
                headers: this.headers,
                body: this.body ?? this.formData,
            })
            .then(response => {
                if (response.ok) {
                    return response;
                } else {
                    throw Error(`${response.status} - ${response.statusText}`);
                }
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