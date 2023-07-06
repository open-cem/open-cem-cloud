import { Toast } from "primereact/toast";

const presentError = (description: string, title?: string, error?:any, toast?: Toast | null) => {
    if (!title) {
        title = "Fehler";
    }
    if (toast) {
        toast.show({
            severity:'error', 
            summary: title, 
            detail: description ?? error,
            life: 10_000
        });
    }
    if (error) {
        console.error(error);
    } else {
        console.log(description);
    }
}

const presentSuccess = (toast: Toast, description: string, title?: string) => {
    if (!title) {
        title = "Erfolg";
    }
    toast.show({
        severity:'success', 
        summary: title, 
        detail: description,
        life: 5_000
    });
}

export { presentError, presentSuccess };