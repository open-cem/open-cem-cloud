import { DropdownInputGroup, InputGroup, NumberInputGroup, SwitchInputGroup } from "../inputGroup/InputGroup";
import { useEffect, useState } from "react";
import { useAuth } from "react-oidc-context";
import { ComponentsService } from "../component/ComponentsService";
import _ from "lodash";

class ParameterMeta {
    name: string;
    label: string;
    type: string;
    listTyp: string | null;
    referenceFamilyId: string | null;

    constructor(name: string, label: string, type: string, listType: string | null, referenceFamilyId: string | null) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.listTyp = listType;
        this.referenceFamilyId = referenceFamilyId;
    }
}

interface ParameterOption {
    id: string;
    name: string;
}

const ParameterInput = ({ meta, value, changeFn, installationId }: { meta: ParameterMeta, value: any, changeFn: (prop: string, value: any) => void, installationId: string }) => {
    const auth = useAuth();
    const [options, setOptions] = useState<ParameterOption[]>([]);
    const [selectedOption, setSelectedOptions] = useState<ParameterOption | undefined>();

    useEffect(() => {
        if (meta.type === "REFERENCE" && meta.referenceFamilyId && auth.user) {
            new ComponentsService()
                .loadComponentsByFamily(installationId, meta.referenceFamilyId, auth.user.access_token)
                .then(cs => {
                    setOptions(_.orderBy(cs, c => c.name));
                    setSelectedOptions(_.find(cs, c => c.id === value));
                });
        }
    }, [auth.user, meta, value, installationId]);

    const selectOption = (option: ParameterOption) => {
        setSelectedOptions(option);
        changeFn(meta.name, option.id);
    };

    if (meta.type === "TEXT") {
        return (
            <InputGroup id={meta.name} label={meta.label} name={meta.name} value={value} changeFn={e => changeFn(meta.name, e.target.value)} />
        );
    } else if (meta.type === "NUMBER") {
        return (
            <NumberInputGroup id={meta.name} label={meta.label} name={meta.name} value={value} changeFn={e => changeFn(meta.name, e.value)} />
        );
    } else if (meta.type === "BOOL") {
        return (
            <SwitchInputGroup id={meta.name} label={meta.label} value={value} onChangeFn={e => changeFn(meta.name, e.value)} />
        );
    } else if (meta.type === "REFERENCE") {
        return (
            <DropdownInputGroup id={meta.name} label={meta.label} value={selectedOption} onChangeFn={e => selectOption(e.value)} options={options} optionLabel="name" />
        );
    } else {
        throw new Error(`The type '${meta.type}' is out of range.`)
    }
}

export { ParameterMeta, ParameterInput };