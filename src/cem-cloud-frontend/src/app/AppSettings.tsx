let apiUri = "";

export const setApiUri = (newUri: string) => {
    // only set the uri if it is still the initial value.
    if (apiUri === "" && newUri && newUri !== "") {
        apiUri = newUri;
    }
}

export const getApiUri = () => apiUri;