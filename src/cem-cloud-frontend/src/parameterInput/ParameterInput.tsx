import { InputGroup } from "../inputGroup/InputGroup";

class ParameterMeta {
    name: string; 
    label: string;
    type: string;
    listTyp: string|null;

    constructor(name: string, label: string, type: string, listType: string|null) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.listTyp = listType;
    }
}

const ParameterInput = ({ meta, value }: { meta: ParameterMeta, value: any }) => {
    if (meta.type === "TEXT" || meta.type === "NUMBER") {
        return (
            <InputGroup id={meta.name} label={meta.label} name={meta.name} value={value} changeFn={() => {}} />
        );
    } else {
        throw new Error(`The type '${meta.type}' is out of range.`)
    }
}

export { ParameterMeta, ParameterInput };