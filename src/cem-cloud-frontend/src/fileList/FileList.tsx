import { Fragment, useEffect, useState } from "react";
import ComponentListEntry from "../componentListEntry/ComponentListEntry";
import { PrimeIcons } from "primereact/api";
import { InputText } from "primereact/inputtext";
import { Panel, PanelHeaderTemplateOptions } from "primereact/panel";
import _ from "lodash";
import "./FileList.css";

type File = string;

const FileList = ({ title, initFiles, deleteFn }: { title: string, initFiles: File[], deleteFn: (file: File) => void }) => {
    const [searchTerm, setSearchTerm] = useState<string>("");
    const [files, setFiles] = useState<File[]>(initFiles);

    useEffect(() => {
        setFiles(initFiles);
    }, [initFiles])

    const onSearchTermChanged = (value: string) => {
        setSearchTerm(value);
        if (value) {
            setFiles(_.filter(initFiles, f => f.toLowerCase().includes(value.toLowerCase())));
        } else {
            setFiles(initFiles);
        }
    };

    const headerTemplate = (options: PanelHeaderTemplateOptions) => {
        const className = options.className;
        const titleClassName = options.titleClassName;

        return (
            <div className={className}>
                <span className={titleClassName}>{title}</span>
                <Fragment>
                    <span className="p-input-icon-left">
                        <i className={PrimeIcons.SEARCH} />
                        <InputText placeholder="Suche" value={searchTerm} onChange={(e) => onSearchTermChanged(e.target.value)}  />
                    </span>
                </Fragment>
            </div>
        )
    };

    return (
        <Panel headerTemplate={headerTemplate}>
            {
                files.length
                    ? files.map(file =>
                        <ComponentListEntry key={file} label={file} deleteAction={() => deleteFn(file)} />
                    )
                    : <p>Keine {title} vorhanden.</p>
            }
        </Panel>
    )
};

export default FileList;
export type { File };