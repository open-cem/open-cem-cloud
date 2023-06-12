import { InputText } from "primereact/inputtext";
import { Image } from "primereact/image";
import "./InputGroup.css";
import { ChangeEventHandler } from "react";

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

export const PictureInputGroup = ({ id, label }: { id: string, label: string }) => {
    return (
        <div className="input-group">
            <label htmlFor={id}>{label}</label>
            <Image id={id} src="/img/placeholder.png" alt="Image" />
        </div>
    )
};