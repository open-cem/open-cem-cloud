import { InputText } from "primereact/inputtext";
import { Image } from "primereact/image";
import "./InputGroup.css";

export const InputGroup = (props: any) => {
    return (
        <div className="input-group">
            <label htmlFor={props.id}>{props.label}</label>
            <InputText id={props.id} value={props.value} onChange={(e) => props.setValue(e.target.value)} />
        </div>
    )
};

export const PictureInputGroup = (props: any) => {
    return (
        <div className="input-group">
            <label htmlFor={props.id}>{props.label}</label>
            <Image id={props.id} src="/img/placeholder.png" alt="Image" />
        </div>
    )
};