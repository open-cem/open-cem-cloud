import { ChangeEventHandler } from "react";
import { InputText } from "primereact/inputtext";
import { InputNumber, InputNumberValueChangeEvent } from 'primereact/inputnumber';
import { Image } from "primereact/image";
import "./InputGroup.css";
import AuthorizedImage from "../authorizedImage/authorizedImage";
import { Installation } from "../installation/InstallationsService";

/**
 * Shows a label and an input field.
 * 
 * @param label The label of the input field
 * @param id The id which connects the label and the input field (for - id)
 * @param changeFn The onChange function of the input field
 * @param name The name of the input field
 * @param value The value of the input field
 * @param isReadOnly True, if the input field is read only, else false
 */
export const InputGroup = ({ label, id, changeFn, name, value, isReadOnly }: { label: string, id: string, changeFn: ChangeEventHandler<HTMLInputElement>, name?: string, value: string | undefined, isReadOnly?: boolean }) => {
    if (!isReadOnly) {
        isReadOnly = false;
    }
    if (!name) {
        name = '';
    }

    return (
        <div className="input-group">
            <label htmlFor={id}>{label}</label>
            <InputText id={id} value={value} name={name} onChange={changeFn} readOnly={isReadOnly} />
        </div>
    )
};

/**
 * Shows a label and a number input field.
 * 
 * @param label The label of the input field
 * @param id The id which connects the label and the input field (for - id)
 * @param changeFn The onChange function of the input field
 * @param name The name of the input field
 * @param value The value of the input field
 * @param isReadOnly True, if the input field is read only, else false
 */
export const NumberInputGroup = ({ label, id, changeFn, name, value, isReadOnly }: { label: string, id: string, changeFn: (event: InputNumberValueChangeEvent) => void, name?: string, value: number, isReadOnly?: boolean }) => {
    if (!isReadOnly) {
        isReadOnly = false;
    }
    if (!name) {
        name = '';
    }

    return (
        <div className="input-group">
            <label htmlFor={id}>{label}</label>
                <InputNumber id={id} value={value} name={name} onValueChange={changeFn} readOnly={isReadOnly} />
        </div>
    )
}

export const PictureInputGroup = ({ id, label, installation }: { id: string, label: string, installation: Installation }) => {
    return (
        <div className="input-group">
            <label htmlFor={id}>{label}</label>
            {
                // blobs do not need to be authorized
                installation.imageUrl.startsWith("blob")
                ? <Image src={installation.imageUrl} />
                : <AuthorizedImage installation={installation} id={id} />
            }
        </div>
    )
};