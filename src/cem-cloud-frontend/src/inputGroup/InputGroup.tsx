import { ChangeEventHandler } from "react";
import { InputText } from "primereact/inputtext";
import { InputNumber, InputNumberChangeEvent } from 'primereact/inputnumber';
import { Image } from "primereact/image";
import { Dropdown, DropdownChangeEvent } from "primereact/dropdown";
import "./InputGroup.css";
import AuthorizedImage from "../authorizedImage/authorizedImage";
import { Installation } from "../installation/InstallationsService";
import { SelectItemOptionsType } from "primereact/selectitem";
import { InputSwitch, InputSwitchChangeEvent } from "primereact/inputswitch";
import { Chips, ChipsChangeEvent } from 'primereact/chips';
import { MultiSelect, MultiSelectChangeEvent } from 'primereact/multiselect';

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
export const NumberInputGroup = ({ label, id, changeFn, name, value, isReadOnly }: { label: string, id: string, changeFn: (event: InputNumberChangeEvent) => void, name?: string, value: number, isReadOnly?: boolean }) => {
    if (!isReadOnly) {
        isReadOnly = false;
    }
    if (!name) {
        name = '';
    }

    return (
        <div className="input-group">
            <label htmlFor={id}>{label}</label>
                <InputNumber id={id} value={value} name={name} onChange={changeFn} readOnly={isReadOnly} maxFractionDigits={2} />
        </div>
    )
};

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

/**
 * Shows a label and a dropdown.
 * 
 * @param label The label of the dropdown
 * @param id The id which connects the label and the dropdown(for - id)
 * @param value The selected value of the dropdown
 * @param onChangeFn The onChange function of the dropdown
 * @param options The dropdown values available for selection
 * @param optionLabel The property name of the object to select as label
 */
export const DropdownInputGroup = ({ label, id, value, onChangeFn, options, optionLabel, disabled}: { label: string, id: string, value: any, onChangeFn: (event: DropdownChangeEvent) => void, options: SelectItemOptionsType, optionLabel: string, disabled?: boolean }) => {
    return (
        <div className="input-group">
            <label htmlFor={id}>{label}</label>
            <Dropdown id={id} placeholder={`${label} auswählen`} value={value} options={options} optionLabel={optionLabel} onChange={onChangeFn} filter showClear disabled={disabled} />
        </div>
    )
};

/**
 * Shows a label and a switch.
 * 
 * @param label The label of the switch
 * @param id The id which connects the label and the dropdown(for -id)
 * @param value True if the input is checked
 * @param onChangeFn The onChange function of the switch
 */
export const SwitchInputGroup = ({label, id, value, onChangeFn, isReadOnly}: { label: string, id: string, value?: boolean, onChangeFn: (event: InputSwitchChangeEvent) => void, isReadOnly?: boolean }) => {
    if (value === undefined) {
        value = false;
    }

    return (
        <div className="input-group">
            <label htmlFor={id}>{label}</label>
            <InputSwitch id={id} checked={value} onChange={onChangeFn} disabled={isReadOnly}/>
        </div>
    )
};

/**
 * Shows a label and a multi value input field.
 * 
 * @param label The label of the input field
 * @param id The id which connects the label and the input field (for - id)
 * @param changeFn The onChange function of the input field
 * @param name The name of the input field
 * @param value The value of the input field
 * @param isReadOnly True, if the input field is read only, else false
 * @param isNumeric True, if the input field should only accept numbers, else false
 */
export const MultiValueInput = ({ label, id, changeFn, name, value, isReadOnly, isNumeric }: { label: string, id: string, changeFn: (event: ChipsChangeEvent) => void, name?: string, value: string[] | undefined, isReadOnly?: boolean, isNumeric?: boolean }) => {
    if (!isReadOnly) {
        isReadOnly = false;
    }

    if (!name) {
        name = '';
    }

    let additionalProps: any = {};
    if (isNumeric) {
        additionalProps["keyfilter"] = 'int';
    }

    return (
        <div className="input-group">
            <label htmlFor={id}>{label}</label>
            <Chips id={id} value={value} name={name} onChange={changeFn} readOnly={isReadOnly} separator="," {... additionalProps} />
        </div>
    )
}


/**
 * Shows a label and a multiselect.
 * 
 * @param label The label of the multiselect
 * @param id The id which connects the label and the multiselect(for - id)
 * @param value The selected value of the multiselect
 * @param onChangeFn The onChange function of the multiselect
 * @param options The multiselect values available for selection
 * @param optionLabel The property name of the object to select as label
 */
export const MultiSelectInputGroup = ({ label, id, value, onChangeFn, options, optionLabel, isReadOnly}: { label: string, id: string, value: any, onChangeFn: (event: MultiSelectChangeEvent) => void, options: SelectItemOptionsType, optionLabel: string, isReadOnly?: boolean }) => {
    return (
        <div className="input-group">
            <label htmlFor={id}>{label}</label>
            <MultiSelect id={id} placeholder={`${label} auswählen`} value={value} options={options} optionLabel={optionLabel} onChange={onChangeFn} filter display="chip" disabled={isReadOnly} />
        </div>
    )
}