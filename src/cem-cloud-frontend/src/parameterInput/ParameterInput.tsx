import { InputGroup, NumberInputGroup } from "../inputGroup/InputGroup";

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

const ParameterInput = ({ meta, value, changeFn }: { meta: ParameterMeta, value: any, changeFn: (prop: string, value: any) => void }) => {

    if (meta.type === "TEXT") {
        return (
            <InputGroup id={meta.name} label={meta.label} name={meta.name} value={value} changeFn={e => changeFn(meta.name, e.target.value)} />
        );
    } else if (meta.type === "NUMBER") {
        return (
            <NumberInputGroup id={meta.name} label={meta.label} name={meta.name} value={value} changeFn={e => changeFn(meta.name, e.target.value)} />
        );
    } else {
        throw new Error(`The type '${meta.type}' is out of range.`)
    }
}

export { ParameterMeta, ParameterInput };