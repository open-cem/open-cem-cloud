import { MultiValueInput, DropdownInputGroup, InputGroup, NumberInputGroup, SwitchInputGroup, MultiSelectInputGroup } from "../inputGroup/InputGroup";
import { useEffect, useState } from "react";
import { useAuth } from "react-oidc-context";
import { ComponentsService } from "../component/ComponentsService";
import _ from "lodash";
import { ComponentWizardConfiguration } from "../app/WizardSetsService";

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

const ParameterInput = ({ meta, value, changeFn, installationId, inputConfig }: { meta: ParameterMeta, value: any, changeFn: (prop: string, value: any) => void, installationId: string, inputConfig?: ComponentWizardConfiguration }) => {
    const auth = useAuth();
    const [options, setOptions] = useState<ParameterOption[]>([]);
    const [selectedOption, setSelectedOption] = useState<ParameterOption | undefined>();
    const [selectedOptions, setSelectedOptions] = useState<ParameterOption[] | undefined>();

    useEffect(() => {
        if ((meta.type === "REFERENCE" || meta.listTyp === "REFERENCE") && meta.referenceFamilyId && auth.user) {
            new ComponentsService()
                .loadComponentsByFamily(installationId, meta.referenceFamilyId, auth.user.access_token)
                .then(cs => {
                    setOptions(_.orderBy(cs, c => c.name));
                    if (meta.listTyp === "REFERENCE") {
                        setSelectedOptions(_.filter(cs, c => (value as string[])?.includes(c.id)));
                    } else {
                        setSelectedOption(_.find(cs, c => c.id === value));
                    }
                });
        }
    }, [auth.user, meta, value, installationId]);

    const selectOption = (option: ParameterOption) => {
        setSelectedOption(option);
        changeFn(meta.name, option.id);
    };

    const selectOptions = (options: ParameterOption[]) => {
        setSelectedOptions(options);
        changeFn(meta.name, options.map(o => o.id));
    };

    if (meta.type === "TEXT") {
        return (
            <InputGroup id={meta.name} label={meta.label} name={meta.name} value={value} changeFn={e => changeFn(meta.name, e.target.value)} isReadOnly={inputConfig?.isFieldReadonly(meta.name)} />
        );
    } else if (meta.type === "NUMBER") {
        return (
            <NumberInputGroup id={meta.name} label={meta.label} name={meta.name} value={value} changeFn={e => changeFn(meta.name, e.value)} isReadOnly={inputConfig?.isFieldReadonly(meta.name)} />
        );
    } else if (meta.type === "BOOL") {
        return (
            <SwitchInputGroup id={meta.name} label={meta.label} value={value} onChangeFn={e => changeFn(meta.name, e.value)} isReadOnly={inputConfig?.isFieldReadonly(meta.name)} />
        );
    } else if (meta.type === "REFERENCE") {
        return (
            <DropdownInputGroup id={meta.name} label={meta.label} value={selectedOption} onChangeFn={e => selectOption(e.value)} options={options} optionLabel="name" disabled={inputConfig?.isFieldReadonly(meta.name)} />
        );
    } else if (meta.type === "LIST") {
        if (meta.listTyp === "TEXT") {
            return (
                <MultiValueInput id={meta.name} label={meta.label} name={meta.name} value={value} changeFn={e => changeFn(meta.name, e.target.value)} isReadOnly={inputConfig?.isFieldReadonly(meta.name)} />
            );
        } else if (meta.listTyp === "NUMBER") {
            return (
                <MultiValueInput id={meta.name} label={meta.label} name={meta.name} value={value} changeFn={e => changeFn(meta.name, e.value)} isNumeric={true} isReadOnly={inputConfig?.isFieldReadonly(meta.name)} />
            );
        } else if (meta.listTyp === "REFERENCE") {
            return (
                <MultiSelectInputGroup id={meta.name} label={meta.label} value={selectedOptions} onChangeFn={e => selectOptions(e.value)} options={options} optionLabel="name" isReadOnly={inputConfig?.isFieldReadonly(meta.name)} />
            );
        } else {
                throw new Error(`The type '${meta.type}' is out of range.`)
            }
    } else {
        throw new Error(`The type '${meta.type}' is out of range.`)
    }
}

export { ParameterMeta, ParameterInput };